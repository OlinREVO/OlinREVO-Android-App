package com.revo.display.views.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.sensors.OrientationChangeListener;
import com.revo.display.sensors.OrientationSensor;
import com.revo.display.views.custom.RBatteryMeter;
import com.revo.display.views.custom.RSpeedometer;


/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment implements OrientationChangeListener {
    RFirebase ref = RevoApplication.app.getFireBaseHelper();

    private OrientationSensor orientationSensor;

    //Views for Display
    RSpeedometer RSpeedometer;
    RBatteryMeter RBatteryMeter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Implement code here to be performed when

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.registerReceiver(new DataReceiver(), new IntentFilter("REVO_APP_DISPLAY"));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        RSpeedometer = (RSpeedometer) rootView.findViewById(R.id.speedometer);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));

        updateMode();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "charge", new String[] {"driver", "charge"});
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "speed", new String[] {"driver", "speed"});

        if (orientationSensor != null) {
            orientationSensor.unregisterSensors();
            orientationSensor.unregisterListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMode();
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {
        // Get data from bluetooth chip & send to firebase
        Firebase firebaseRef = ref.getRef(new String[] {"driver"});

        // This should happen in bluetooth callback
        firebaseRef.child("charge").setValue(RBatteryMeter != null ? RBatteryMeter.getCurrentCharge() : 0);
        firebaseRef.child("speed").setValue(RSpeedometer != null ? RSpeedometer.getCurrentSpeed() : 0);

        orientationSensor = new OrientationSensor(getActivity());
        orientationSensor.registerListener(this);
    }

    @Override
    public void setupNotDriverMode() {
        // Get data from firebase
        ref.registerListener(DriverFragment.class.getSimpleName() + "charge", new String[] {"driver", "charge"}, new ValueCallback() {
            @Override
            public void handleValue(long value) {
                if (RBatteryMeter != null) {
                    RBatteryMeter.onValueChanged(value);
                }
            }
        });

        ref.registerListener(DriverFragment.class.getSimpleName() + "speed", new String[] {"driver", "speed"}, new ValueCallback() {
            @Override
            public void handleValue(long value) {
                if (RSpeedometer != null) {
                    RSpeedometer.onValueChanged(value);
                }
            }
        });
    }

    @Override
    public void onDirectionChange(float direction) {
        Firebase firebaseRef = ref.getRef(new String[] {"driver"});
        firebaseRef.child("direction").setValue((long) direction);
    }

}
