package com.revo.display.views.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.views.custom.Speedometer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container);

        Speedometer speedometer = (Speedometer) rootView.findViewById(R.id.speedometer);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
