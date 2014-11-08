package com.revo.display.views.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.Bluetooth;
import com.revo.display.bluetooth.ChangeListener;
import com.revo.display.bluetooth.DataWatcher;
import com.revo.display.network.FirebaseHelper;
import com.revo.display.sensors.OrientationChangeListener;
import com.revo.display.sensors.OrientationSensor;
import com.revo.display.views.custom.Compass;
import com.revo.display.views.custom.Speedometer;

/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment implements OrientationChangeListener {
    FirebaseHelper firebaseHelper = RevoApplication.app.getFireBaseHelper();
    private OrientationSensor orientationSensor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        final Speedometer speedometer = (Speedometer) rootView.findViewById(R.id.speedometer);
        return rootView;
    }

    public void onResume() {
        super.onResume();
        if (isDriver()) {
            orientationSensor = new OrientationSensor(getActivity());
            orientationSensor.registerListener(this);
        }
    }

    public void onPause() {
        super.onPause();
        if (isDriver()) {
            orientationSensor.unregisterSensors();
        }
    }

    public void onDirectionChange(float direction) {
        Log.d(tag(), "Writing new direction to Firebase");
        firebaseHelper.write(DIRECTION_KEY, direction);
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }
}
