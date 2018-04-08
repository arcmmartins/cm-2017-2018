package pt.ua.cm.smartdomotics.connectivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import pt.ua.cm.smartdomotics.MainActivity;
import pt.ua.cm.smartdomotics.R;
import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;

/**
 * Created by alvaro on 21-10-2017.
 */

public class ZeroMQSubscriverService extends IntentService {

    private ZContext context;
    private ZMQ.Socket subscriber;
    public final static String SENSOR_SUBSCRIPTION = "sensor";
    public final static String DB_SUBSCRIPTION = "db";
    private final DataRepo repo = DataRepo.getInstance();
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ZeroMQSubscriverService(String name) {
        super(name);
    }

    public ZeroMQSubscriverService(){
        super("ZeroMQSubscriverService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        SharedPreferences mShared;
        mShared = PreferenceManager.getDefaultSharedPreferences(this);
        String ip = mShared.getString("smartdomotics_ip", "localhost");
        context = new ZContext();
        subscriber = context.createSocket(ZMQ.SUB);
        subscriber.connect("tcp://"+ip+":9595");
    }

    @Override
    public void onHandleIntent(Intent workIntent) {
        subscriber.subscribe(SENSOR_SUBSCRIPTION.getBytes(ZMQ.CHARSET));
        subscriber.subscribe(DB_SUBSCRIPTION.getBytes(ZMQ.CHARSET));
        Log.d("subscriver service", "started subscription");

        while (true) {
            try {
                String topic = subscriber.recvStr();
                String msg = subscriber.recvStr();

                if (topic.equals(SENSOR_SUBSCRIPTION)) {
                    SensorEntity se = repo.findSE("temperature", 1);
                    se.setValue(Integer.parseInt(msg));
                    repo.updateSE(se);

                    if(Integer.parseInt(msg) > 35){
                        Intent intent = new Intent(this, MainActivity.class);
                        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
                        Notification n  = new Notification.Builder(this)
                                .setContentTitle("Warning")
                                .setContentText("Temperature reached unsafe levels")
                                .setSmallIcon(R.drawable.cloud)
                                .setContentIntent(pIntent)
                                .setAutoCancel(true).build();
                        NotificationManager notificationManager =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        notificationManager.notify(0, n);

                    }

                } else if (topic.equals(DB_SUBSCRIPTION)) {
                    Log.d("db", msg);
                    try {
                        JSONObject json = new JSONObject(msg);
                        if(json.has("lights")){
                            JSONArray lights = json.getJSONArray("lights");

                            for (int i = 0; i < lights.length(); i++) {
                                BooleanEntity be = repo.findBE(lights.getJSONObject(i).getString("name"), lights.getJSONObject(i).getInt("did"));
                                if (be != null) {
                                    be.setStatus(lights.getJSONObject(i).getBoolean("status"));
                                    repo.updateBE(be);
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
                                }
                            }
                        }


                    } catch (JSONException e) {
                        Log.e("json parser", e.getMessage());
                    }
                }
            }catch (Exception e){
                //
            }
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        context.close();
    }
}
