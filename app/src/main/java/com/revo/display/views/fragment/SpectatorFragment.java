package com.revo.display.views.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.revo.display.R;
import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.ValuesCallback;
import com.revo.display.network.RFirebase;
import com.revo.display.network.ValueCallback;
import com.revo.display.views.custom.Compass;

import java.util.HashMap;

/**
 * Created by sihrc on 9/20/14.
 */
public class SpectatorFragment extends RevoFragment {
    private RFirebase ref = RevoApplication.app.getFireBaseHelper();
    private Compass compass;
    private MapView map;
    private PolylineOptions polylineOptions;
    private LatLng currCoords;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.spectator_fragment, container, false);
        compass = (Compass) rootView.findViewById(R.id.compass);

        map = (MapView) rootView.findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());
        polylineOptions = new PolylineOptions().geodesic(true);

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
                    handleCoordinateReceived(value);
                }
            }
        });
    }

    private void handleCoordinateReceived(Object value) {
        try {
            HashMap<String, Double> coordMap = (HashMap<String, Double>) value;
            LatLng coord = new LatLng(coordMap.get("latitude"), coordMap.get("longitude"));
            updateCoords(coord);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    private void updateCoords(LatLng coord) {
        if (coord != null && newCoords(coord)) {
            currCoords = coord;
            addMapPosition(currCoords);
        }
    }

    private boolean newCoords(LatLng coord) {
        return coord != null &&
                (currCoords == null ||
                        (coord.latitude  != currCoords.latitude ||
                         coord.longitude != currCoords.longitude));
    }

    private void addMapPosition(LatLng coord) {
        GoogleMap gmap = map.getMap();
        gmap.clear();
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, 2));

        polylineOptions.add(coord);
        gmap.addPolyline(polylineOptions);
    }

    @Override
    public ValuesCallback getValuesCallback() {
        return null;
    }
}
