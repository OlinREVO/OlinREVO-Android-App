package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.views.custom.Speedometer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container);

        Speedometer speedometer = (Speedometer) rootView.findViewById(R.id.speedometer);
        speedometer.setCurrentSpeed(50);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
