package pt.ua.cm.smartdomotics.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pt.ua.cm.smartdomotics.R;
import pt.ua.cm.smartdomotics.connectivity.IMessageListener;
import pt.ua.cm.smartdomotics.connectivity.MessageListenerHandler;
import pt.ua.cm.smartdomotics.connectivity.ZeroMQMessageTask;
import pt.ua.cm.smartdomotics.database.SmartDomoticsDatabase;
import pt.ua.cm.smartdomotics.database.entities.BooleanEntity;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;
import pt.ua.cm.smartdomotics.database.viewmodels.BEntitiesViewModel;
import pt.ua.cm.smartdomotics.fragments.interfaces.OnFragmentInteractionListener;

public class Room extends Fragment {
    private OnFragmentInteractionListener mListener;
    private DataRepo repo = DataRepo.getInstance();
    private int division_id;
    private volatile View v;
    public final static String NAME = "room";
    private final MessageListenerHandler roomMessageHandler = new MessageListenerHandler(
            new IMessageListener() {
                @Override
                public void messageReceived(String messageBody) {
                    Log.d("received", messageBody);
                }
            },
            ZeroMQMessageTask.MESSAGE_PAYLOAD_KEY);
    public Room() {
        // Required empty public constructor
    }
    public static Room newInstance(String param1, String param2) {
        Room fragment = new Room();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        division_id = repo.findDivision(NAME).getUid();
        BEntitiesViewModel model = ViewModelProviders.of(this).get(BEntitiesViewModel.class);
        model.getEntities(division_id).observe(this, new Observer<List<BooleanEntity>>() {
            @Override
            public void onChanged(@Nullable List<BooleanEntity> booleanEntities) {
                if(v == null)
                    return;
                for(BooleanEntity be : booleanEntities){
                    if(be.getDid()!= 3)
                        continue;
                    if(be.getName().equals("chandelier")){
                        ((Switch)v.findViewById(R.id.master_chandelier_switch)).setChecked(be.isStatus());
                    }else if(be.getName().equals("light")){
                        ((Switch)v.findViewById(R.id.master_light_switch)).setChecked(be.isStatus());
                    }
                }
            }
        });
        populateRoom();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_room, container, false);
        ((Switch)v.findViewById(R.id.master_chandelier_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject json = new JSONObject();
                    BooleanEntity be = repo.findBE("chandelier", 3);
                    JSONArray lights = new JSONArray();
                    JSONObject kitchen_light = new JSONObject();
                    kitchen_light.put("name", be.getName());
                    kitchen_light.put("did", be.getDid());
                    kitchen_light.put("status", isChecked);
                    lights.put(kitchen_light);
                    json.put("lights", lights);
                    if(isChecked != be.isStatus()) {
                        Log.d("zmq message task",json.toString());
                        SharedPreferences mShared;
                        mShared = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String ip = mShared.getString("smartdomotics_ip", "localhost");
                        new ZeroMQMessageTask(roomMessageHandler, ip).execute(json.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        ((Switch)v.findViewById(R.id.master_light_switch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    JSONObject json = new JSONObject();
                    BooleanEntity be = repo.findBE("light", 3);
                    JSONArray lights = new JSONArray();
                    JSONObject kitchen_light = new JSONObject();
                    kitchen_light.put("name", be.getName());
                    kitchen_light.put("did", be.getDid());
                    kitchen_light.put("status", isChecked);
                    lights.put(kitchen_light);
                    json.put("lights", lights);
                    if(isChecked != be.isStatus()) {
                        SharedPreferences mShared;
                        mShared = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        String ip = mShared.getString("smartdomotics_ip", "localhost");
                        Log.d("zmq message task",json.toString());
                        new ZeroMQMessageTask(roomMessageHandler, ip).execute(json.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return v;
    }

    private void populateRoom() {
        if(repo.findBE("chandelier", 3) == null) { //room is division id 3
            BooleanEntity be = new BooleanEntity();
            be.setType(0); // type 0 is light
            be.setName("chandelier");
            be.setDid(3);
            be.setStatus(false);
            repo.insertBE(be);
        }
        if(repo.findBE("light", 3) == null) { //kitchen is division id 2
            BooleanEntity be = new BooleanEntity();
            be.setType(0);
            be.setName("light");
            be.setDid(3);
            be.setStatus(false);
            repo.insertBE(be);
        }

    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
