package com.revo.display.sensors;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.revo.display.network.ValueCallback;

import java.util.ArrayList;


/**
 * Created by isaac on 1/31/15.
 */
public class GPSSensor implements LocationListener {
    private static final long TIME_LIMIT = 5;
    // TODO: I have no idea if this value is reasonable
    private static final long ACCURACY_LIMIT = 100;

    private Context context;
    private LocationManager locationManager;
    private Location currentLocation;
    private ArrayList<ValueCallback> listeners;

    public GPSSensor(Context context) {
        Log.d("gpsSensor", "gpsSensor created");
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        listeners = new ArrayList<ValueCallback>();
    }

    public void deregisterGPS() {
        locationManager.removeUpdates(this);
    }

    /**
     * Returns the more accurate location based on time and accuracy
     * @param newLoc the newer Location
     * @param oldLoc the older Location
     * @return the "better" location
     */
    private Location betterLocation(Location oldLoc, Location newLoc) {
        Log.d("gpsSensor", "Checking for better location");
        // Check for null Locations
        if (newLoc == null && oldLoc == null) return null;
        if (newLoc == null) return oldLoc;
        if (oldLoc == null) return newLoc;

        long timeDiff = newLoc.getTime() - oldLoc.getTime();
        boolean muchNewer = timeDiff > TIME_LIMIT;
        boolean newer = timeDiff >= 0;

        float accuracyDiff = newLoc.getAccuracy() - oldLoc.getAccuracy();
        boolean muchLessAccurate = accuracyDiff < -ACCURACY_LIMIT;
        boolean moreAccurate = accuracyDiff >= 0;

        if (muchNewer && moreAccurate) return newLoc;
        else if (newer && moreAccurate) return newLoc;
        else if (muchNewer && !muchLessAccurate) return newLoc;
        else return oldLoc;
    }

    public void registerListener(ValueCallback callback) {
        listeners.add(callback);
    }

    public void deregisterListener(ValueCallback callback) {
        listeners.remove(callback);
    }

    private void notifyListeners(Location loc) {
        for (ValueCallback callback : listeners) {
            callback.handleValue(loc);
        }
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        Log.d("gpsSensor", "New Location!!!");
        Log.d("gpsSensor", "lat: " + newLocation.getLatitude() + ", long: " + newLocation.getLongitude());
        currentLocation = betterLocation(currentLocation, newLocation);
        notifyListeners(currentLocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
