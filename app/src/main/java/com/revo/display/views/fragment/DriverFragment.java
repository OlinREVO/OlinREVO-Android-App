package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.Bluetooth;
import com.revo.display.bluetooth.ChangeListener;
import com.revo.display.bluetooth.DataWatcher;
import com.revo.display.network.FirebaseHelper;
import com.revo.display.views.custom.Speedometer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    FirebaseHelper firebaseHelper = RevoApplication.app.getFireBaseHelper();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container);

        final Speedometer speedometer = (Speedometer) rootView.findViewById(R.id.speedometer);
        return rootView;
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
