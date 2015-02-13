package com.revo.display.views.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.HashMap;

/**
 * Created by isaac on 2/12/15.
 */
public class RevoMap extends MapView {
    private static float DEFAULT_CAMERA_ZOOM = 14.0f;

    private PolylineOptions polylineOptions;
    private LatLng currCoords;
    private float cameraZoom;

    public RevoMap(Context context) {
        super(context);
        setupMap(context);
    }

    public RevoMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupMap(context);
    }

    private void setupMap(Context context) {
        polylineOptions = new PolylineOptions().geodesic(true);
        MapsInitializer.initialize(context);
        setZoomListener();
        cameraZoom = DEFAULT_CAMERA_ZOOM;
    }

    // save the users zoom level
    private void setZoomListener() {

        getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        cameraZoom = cameraPosition.zoom;
                    }

                });

            }

        });
    }

    public void handleCoordinate(Object value) {
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
        GoogleMap gmap = getMap();
        gmap.clear();
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(coord, cameraZoom));

        polylineOptions.add(coord);
        gmap.addPolyline(polylineOptions);
    }

}
