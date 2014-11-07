package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.sensors.OrientationSensor;
import com.revo.display.views.custom.Compass;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {

    private Compass compass;
    private OrientationSensor orientationSensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        compass = (Compass) rootView.findViewById(R.id.compass);
        return rootView;
    }

    public void onResume() {
        super.onResume();
        orientationSensor = new OrientationSensor(getActivity());
        orientationSensor.registerListener(compass);
    }

    public void onPause() {
        super.onPause();
        orientationSensor.unregisterSensors();
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }
}
