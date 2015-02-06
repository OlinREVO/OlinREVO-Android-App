package com.revo.display.views.fragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.views.custom.Compass;

import java.util.List;
import java.util.Map;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    private RFirebase ref = RevoApplication.app.getFireBaseHelper();
    private Compass compass;
    private MapView map;
    private Polyline kartPathPolyLine;
    private LatLng currCoords;

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
                        LatLng coords = (LatLng) value;
                        if (coords != null) updateCoords(coords);
                    } catch (ClassCastException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void updateCoords(LatLng coords) {
        if (coords != null && newCoords(coords)) {
            currCoords = coords;
            addMapPosition(currCoords);
        }
    }

    private boolean newCoords(LatLng coords) {
        return coords != null &&
                (currCoords == null ||
                 currCoords.latitude != coords.latitude &&
                 currCoords.longitude != coords.longitude);
    }

    private void addMapPosition(LatLng coords) {
        if (kartPathPolyLine == null) {
            GoogleMap gmap = map.getMap();
            Log.d(tag(), coords.toString());
            kartPathPolyLine = gmap.addPolyline(new PolylineOptions().add(coords));
        } else {
            // add new line from previous coord to current coord
            List<LatLng> pathPoints = kartPathPolyLine.getPoints();
            pathPoints.add(coords);
            Log.d(tag(), pathPoints.toString());
            kartPathPolyLine.setPoints(pathPoints);
        }
    }

    @Override
    public ValuesCallback getValuesCallback() {
        return null;
    }
}
