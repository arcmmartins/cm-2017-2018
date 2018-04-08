package pt.ua.cm.smartdomotics;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.kontakt.sdk.android.common.profile.RemoteBluetoothDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ua.cm.smartdomotics.QRcode.BarcodeCaptureActivity;
import pt.ua.cm.smartdomotics.connectivity.IMessageListener;
import pt.ua.cm.smartdomotics.connectivity.MessageListenerHandler;
import pt.ua.cm.smartdomotics.connectivity.ZeroMQMessageTask;
import pt.ua.cm.smartdomotics.connectivity.ZeroMQSubscriverService;
import pt.ua.cm.smartdomotics.database.SmartDomoticsDatabase;
import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.entities.Division;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;
import pt.ua.cm.smartdomotics.fragments.Room;
import pt.ua.cm.smartdomotics.fragments.Dashboard;
import pt.ua.cm.smartdomotics.fragments.Kitchen;
import pt.ua.cm.smartdomotics.fragments.interfaces.OnFragmentInteractionListener;
import pt.ua.cm.smartdomotics.service.BackgroundScanService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnFragmentInteractionListener{
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 143;
    private BottomNavigationView navigation;
    private DataRepo repo = DataRepo.getInstance();
    private final MessageListenerHandler clientMessageHandler = new MessageListenerHandler(
            new IMessageListener() {
                @Override
                public void messageReceived(String messageBody) {
                    Log.d("received", messageBody);
                    if(!messageBody.equals("Ack")){
                        try {
                            syncDB(messageBody);
                        } catch (JSONException e) {
                            Log.d("sync", "invalid json: " + messageBody);
                        }
                    }
                }
            },
            ZeroMQMessageTask.MESSAGE_PAYLOAD_KEY);

    private void syncDB(String messageBody) throws JSONException {
        JSONObject json = new JSONObject(messageBody);
        if(json.has("lights")){
            JSONArray lights = json.getJSONArray("lights");
            for (int i = 0; i < lights.length(); i++) {
                BooleanEntity be = repo.findBE(lights.getJSONObject(i).getString("name"), lights.getJSONObject(i).getInt("did"));
                if (be != null) {
                    be.setStatus(lights.getJSONObject(i).getBoolean("status"));
                    repo.updateBE(be);
                }else{
                    BooleanEntity newBe = new BooleanEntity();
                    newBe.setStatus(lights.getJSONObject(i).getBoolean("status"));
                    newBe.setName(lights.getJSONObject(i).getString("name"));
                    newBe.setDid(lights.getJSONObject(i).getInt("did"));
                    repo.insertBE(newBe);
                }
            }
        }
        if(json.has("alarms")){
            JSONArray alarms = json.getJSONArray("alarms");
            for (int i = 0; i < alarms.length(); i++) {
                BooleanEntity be = repo.findBE(alarms.getJSONObject(i).getString("name"), alarms.getJSONObject(i).getInt("did"));
                if (be != null) {
                    be.setStatus(alarms.getJSONObject(i).getBoolean("status"));
                    repo.updateBE(be);
                }else{
                    BooleanEntity newBe = new BooleanEntity();
                    newBe.setStatus(alarms.getJSONObject(i).getBoolean("status"));
                    newBe.setName(alarms.getJSONObject(i).getString("name"));
                    newBe.setDid(alarms.getJSONObject(i).getInt("did"));
                    repo.insertBE(newBe);
                }
            }
        }
    }

    private Intent serviceIntent;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_kitchen:
                    transaction.replace(R.id.fragment_content, new Kitchen());
                    break;
                case R.id.navigation_dashboard:
                    transaction.replace(R.id.fragment_content, new Dashboard());
                    break;
                case R.id.navigation_room:
                    transaction.replace(R.id.fragment_content, new Room());
                    break;
            }
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (findViewById(R.id.fragment_content) != null) {
            if (savedInstanceState != null) {
                return;
            }
            Dashboard firstFragment = new Dashboard();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, firstFragment).commit();
        }
        repo.init(SmartDomoticsDatabase.getDatabase(this));
        populateDivisions();
        printDB();
        SharedPreferences mShared;
        mShared = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = mShared.getString("smartdomotics_ip", "localhost");
        if(mShared.contains("smartdomotics_ip")){
            startService(new Intent(this, ZeroMQSubscriverService.class));
            new ZeroMQMessageTask(clientMessageHandler, ip).execute("sync");
        }
        serviceIntent = new Intent(getApplicationContext(), BackgroundScanService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Register Broadcast receiver that will accept results from background scanning
        IntentFilter intentFilter = new IntentFilter(BackgroundScanService.ACTION_DEVICE_DISCOVERED);
        registerReceiver(scanningBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(scanningBroadcastReceiver);
        super.onPause();
    }

    private void printDB() {
        Log.d("entities", "printing DB");
        if(repo.getBE().getValue() != null){
            for(BooleanEntity be : repo.getBE().getValue()){
                Log.d("entities",be.toString());
            }
        }
        if(repo.getSE().getValue()!= null){
            for(SensorEntity se : repo.getSE().getValue()){
                Log.d("entities",se.toString());
            }
        }
        if(repo.getD().getValue()!= null){
            for(Division d : repo.getD().getValue()){
                Log.d("entities",d.toString());
            }
        }
    }



    private void populateDivisions() {
        if(repo.findDivision("universal") == null){ // house independente division
            Division d = new Division();
            d.setName("universal");
            repo.insertD(d);
        }
        if(repo.findDivision(Kitchen.NAME) == null){
            Division d = new Division();
            d.setName(Kitchen.NAME);
            repo.insertD(d);
        }
        if(repo.findDivision(Room.NAME) == null){
            Division d = new Division();
            d.setName(Room.NAME);
            repo.insertD(d);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // do nothings for now
    }


    /*
    taken from google barcode reader samples:
    link:
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    SharedPreferences mShared;
                    SharedPreferences.Editor mEdit;
                    mShared = PreferenceManager.getDefaultSharedPreferences(this);
                    String ip = barcode.displayValue; // ip aqui
                    mEdit = mShared.edit();
                    mEdit.putString( "smartdomotics_ip", ip);
                    mEdit.commit();
                    Intent myService = new Intent(MainActivity.this, ZeroMQSubscriverService.class);
                    stopService(myService);
                    startService(new Intent(this, ZeroMQSubscriverService.class));
                    new ZeroMQMessageTask(clientMessageHandler, ip).execute("sync");
                    Log.d("main activity", "Barcode read: " + barcode.displayValue);
                } else {
                    Log.d("main activity", "No barcode captured, intent data is null");
                }
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private final BroadcastReceiver scanningBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Device discovered!
            int devicesCount = intent.getIntExtra(BackgroundScanService.EXTRA_DEVICES_COUNT, 0);
            RemoteBluetoothDevice device = intent.getParcelableExtra(BackgroundScanService.EXTRA_DEVICE);
            double i = device.getDistance();
            Log.d("beacon", device.getAddress());
            if(device.getAddress().equals("C3:D3:9A:75:8E:37")){
                navigation.setSelectedItemId(R.id.navigation_room);
                Toast.makeText(context, "You are in the room", Toast.LENGTH_SHORT).show();
                Log.d("beacon", "beacon is at distance: " +i);
            }
            //statusText.setText(String.format("Total discovered devices: %d\n\nLast scanned device:\n%s", devicesCount, device.toString()));
        }
    };


}
