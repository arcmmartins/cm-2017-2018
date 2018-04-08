package pt.ua.cm.smartdomotics.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.ua.cm.smartdomotics.R;
import pt.ua.cm.smartdomotics.database.SmartDomoticsDatabase;
import pt.ua.cm.smartdomotics.database.entities.SensorEntity;
import pt.ua.cm.smartdomotics.database.repositories.DataRepo;
import pt.ua.cm.smartdomotics.database.viewmodels.SEntitiesViewModel;
import pt.ua.cm.smartdomotics.fragments.interfaces.OnFragmentInteractionListener;

public class Dashboard extends Fragment {
    private OnFragmentInteractionListener mListener;
    private DataRepo repo = DataRepo.getInstance();
    private TextView temp_value;
    public Dashboard() {
        // Required empty public constructor
    }
    public static Dashboard newInstance() {
        Dashboard fragment = new Dashboard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SEntitiesViewModel model = ViewModelProviders.of(this).get(SEntitiesViewModel.class);
        if(repo.findSE("temperature",1) == null){
            SensorEntity se = new SensorEntity();
            se.setDid(1);
            se.setName("temperature");
            se.setValue(33);
            repo.insertSE(se);
        }
        model.getEntities().observe(this, new Observer<List<SensorEntity>>() {
            @Override
            public void onChanged(@Nullable List<SensorEntity> seList) {
                if(!seList.isEmpty()){
                    temp_value.setText(seList.get(0).getValue() + " C");
                }

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        temp_value = (TextView) v.findViewById(R.id.dashboard_temp_value);
        String temp_holder = repo.findSE("temperature",1).getValue() + " C";
        temp_value.setText(temp_holder);
        return v;
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
