package com.revo.display.views.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.DataReceiver;
import com.revo.display.bluetooth.ValuesCallback;
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
    Firebase firebaseRef = ref.getRef(new String[]{"driver"});

    Activity activity;

    private OrientationSensor orientationSensor;

    //Views for Display
    RSpeedometer RSpeedometer;
    RBatteryMeter RBatteryMeter;

    boolean registered = false;
    BroadcastReceiver receiver = new DataReceiver(new ValuesCallback() {
        @Override
        public void handleValues(byte[] values) {
            // Get data from bluetooth chip & send to firebase
            if (RSpeedometer != null && RBatteryMeter != null) {
                RSpeedometer.setCurrentSpeed((float) values[0]);
                RBatteryMeter.setCurrentCharge((float) values[1]);
            }

            // This should happen in bluetooth callback
            if (firebaseRef != null) {
                firebaseRef.child("charge").setValue(values[0]);
                firebaseRef.child("speed").setValue(values[1]);
            }
        }
    });

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        RSpeedometer = (RSpeedometer) rootView.findViewById(R.id.speedometer);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));

        updateMode();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMode();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (activity != null && receiver != null && registered) {
            activity.unregisterReceiver(receiver);
        }
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "charge", new String[]{"driver", "charge"});
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "speed", new String[]{"driver", "speed"});

        if (orientationSensor != null) {
            orientationSensor.unregisterSensors();
            orientationSensor.unregisterListener(this);
        }
    }

    @Override
    public String tag() {
        return DriverFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {
        if (receiver != null && activity != null && !registered) {
            registered = true;
            activity.registerReceiver(receiver, new IntentFilter("REVO_APP_DISPLAY"));
        }

        orientationSensor = new OrientationSensor(getActivity());
        orientationSensor.registerListener(this);


        if (ref != null) {
            ref.deregisterListener(DriverFragment.class.getSimpleName() + "charge", new String[]{"driver", "charge"});
            ref.deregisterListener(DriverFragment.class.getSimpleName() + "speed", new String[]{"driver", "speed"});
        }
    }

    @Override
    public void setupNotDriverMode() {
        // Unregister from driving mode
        if (activity != null && receiver != null && registered) {
            activity.unregisterReceiver(receiver);
            registered = false;
        }

        // Get data from firebase
        ref.registerListener(DriverFragment.class.getSimpleName() + "charge", new String[]{"driver", "charge"}, new ValueCallback() {
            @Override
            public void handleValue(long value) {
                if (RBatteryMeter != null) {
                    RBatteryMeter.onValueChanged(value);
                }
            }
        });

        ref.registerListener(DriverFragment.class.getSimpleName() + "speed", new String[]{"driver", "speed"}, new ValueCallback() {
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
        firebaseRef.child("direction").setValue(direction);
    }
}
