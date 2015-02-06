package com.revo.display.views.fragment;

import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.model.LatLng;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.sensors.GPSSensor;
import com.revo.display.sensors.OrientationSensor;
import com.revo.display.views.custom.RBatteryMeter;
import com.revo.display.views.custom.RSpeedometer;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by sihrc on 9/20/14.
 */
public class DriverFragment extends RevoFragment {
    RFirebase ref = RevoApplication.app.getFireBaseHelper();
    Firebase firebaseRef = ref.getRef(new String[]{"driver"});

    private OrientationSensor orientationSensor;
    private GPSSensor gpsSensor;

    //Views for Display
    RSpeedometer RSpeedometer;
    RBatteryMeter RBatteryMeter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        RSpeedometer = (RSpeedometer) rootView.findViewById(R.id.speedometer);
        RBatteryMeter = (RBatteryMeter) rootView.findViewById((R.id.batterymeter));

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "charge", new String[]{"driver", "charge"});
        ref.deregisterListener(DriverFragment.class.getSimpleName() + "speed", new String[]{"driver", "speed"});

        if (orientationSensor != null) {
            orientationSensor.unregisterSensors();
            orientationSensor.unregisterListeners();
        }

        removeGPS();
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

        if (orientationSensor == null) {
            orientationSensor = new OrientationSensor(getActivity());
            orientationSensor.registerListener(new ValueCallback() {
                @Override
                public void handleValue(Object value) {
                    Log.d("New Direction", ((Long) value).toString());
                    firebaseRef.child("direction").setValue((Long) value);
                }
            });
        }

        setupGPS();

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

        if (orientationSensor != null) {
            orientationSensor.unregisterSensors();
            orientationSensor.unregisterListeners();
        }

        removeGPS();

        // Get data from firebase
        ref.registerListener(DriverFragment.class.getSimpleName() + "charge", new String[]{"driver", "charge"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                if (RBatteryMeter != null) {
                    RBatteryMeter.onValueChanged((Long) value);
                }
            }
        });

        ref.registerListener(DriverFragment.class.getSimpleName() + "speed", new String[]{"driver", "speed"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                if (RSpeedometer != null) {
                    RSpeedometer.onValueChanged((Long) value);
                }
            }
        });
    }

    private void setupGPS() {
        if (gpsSensor == null) {
            gpsSensor = new GPSSensor(getActivity());
            gpsSensor.registerListener(new ValueCallback() {
                @Override
                public void handleValue(Object value) {
                    Location loc = (Location) value;
                    double latitude = loc.getLatitude();
                    double longitude = loc.getLongitude();

                    Log.d(tag(), "Sending new Coordinates");
                    Log.d(tag(), "Latitude:  " + latitude);
                    Log.d(tag(), "Longitude: " + longitude);

                    LatLng coords = new LatLng(latitude, longitude);
                    firebaseRef.child("coordinates").setValue(coords);
                }
            });
        }
    }

    private void removeGPS() {
        if (gpsSensor != null) {
            gpsSensor.deregisterGPS();
            gpsSensor = null;
        }
    }

    @Override
    public ValuesCallback getValuesCallback() {
        return new ValuesCallback() {
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
        };
    }
}
