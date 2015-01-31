package com.revo.display.views.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.MapView;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.views.custom.Compass;

import java.util.Map;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    private RFirebase ref = RevoApplication.app.getFireBaseHelper();
    private Compass compass;
    private MapView map;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        compass = (Compass) rootView.findViewById(R.id.compass);
        map = (MapView) rootView.findViewById(R.id.map);

        map.onCreate(savedInstanceState);

        updateMode();
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
        ref.deregisterListener(SpectatorFragment.class.getSimpleName() + "direction", new String[] {"driver", "direction"});
        ref.deregisterListener(SpectatorFragment.class.getSimpleName() + "coordinates", new String[] {"driver", "coordinates"});
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        updateMode();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    @Override
    public String tag() {
        return SpectatorFragment.class.getSimpleName();
    }

    @Override
    public void setupDriverMode() {
        generalSetup();
    }

    @Override
    public void setupNotDriverMode() {
        generalSetup();
    }

    private void generalSetup() {
        // Get data from firebase
        ref.registerListener(SpectatorFragment.class.getSimpleName() + "direction", new String[] {"driver", "direction"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                if (compass != null) {
                    compass.onValueChanged((Long) value);
                }
            }
        });

        ref.registerListener(SpectatorFragment.class.getSimpleName() + "coordinates", new String[] {"driver", "coordinates"}, new ValueCallback() {
            @Override
            public void handleValue(Object value) {
                if (map != null) {
                    try {
                        Map<String, Object> coords = (Map<String, Object>) value;
                        double lat = (Double) coords.get("latitude");
                        double lon = (Double) coords.get("longitude");
                        addMapPosition(lat, lon);
                    } catch (ClassCastException e) {
                        Log.d(tag() + " handle coordinate", "Problem casting the Map<String, Object>");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addMapPosition(double lat, double lon) {
        if (map != null) {
            // add new line from previous coord to current coord
        }
    }

    @Override
    public ValuesCallback getValuesCallback() {
        return null;
    }
}
