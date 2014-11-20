package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.RFirebase;
import com.revo.display.views.custom.RBatteryMeter;
import com.revo.display.views.custom.RSpeedometer;

import java.util.Timer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    RFirebase RFirebase = RevoApplication.app.getFireBaseHelper();

    //Views for Display
    RSpeedometer RSpeedometer;
    RBatteryMeter RBatteryMeter;

    //For updating the speedometer
    Timer timer;
    boolean timerRunning;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        RSpeedometer = (RSpeedometer) rootView.findViewById(R.id.speedometer);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timerRunning && timer != null) {
            timerRunning = false;
            timer.cancel();
        }
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
