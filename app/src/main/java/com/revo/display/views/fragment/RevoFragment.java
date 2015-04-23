package com.revo.display.views.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.view.View;

import com.revo.display.RevoApplication;
import com.revo.display.bluetooth.DataReceiver;
import com.revo.display.bluetooth.ValuesCallback;

/**
 * Created by sihrc on 9/20/14.
 */
public abstract class RevoFragment extends Fragment {
    Activity activity;
    boolean registered = false;
    BroadcastReceiver receiver;

    public void updateMode() {
        if (RevoApplication.isDriver)
            setupDriverMode();
        else
            setupNotDriverMode();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        receiver = new DataReceiver(getValuesCallback());
        updateMode();
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
            registered = false;
        }
    }

    /**
     * Converts the array of bytes (which represent a string of hex values)
     * received from bluetooth into an array of integer values.
     * There are two bytes per integer value
     * @param bytes that represent an ascii string of hex
     * @return an array of the bytes integer values
     */
    public int[] bytesToValues(byte[] bytes) {
        // create the hex string sent over bluetooth
        String hexString = new String(bytes);

        // split the hex string into the individual values
        int numVals = bytes.length / 2;
        String[] byteStrings = new String[numVals];
        for (int i = 0; i < numVals; ++i) {
            int strStart = i * 2;
            int strEnd = strStart + 2;
            byteStrings[i] = hexString.substring(strStart, strEnd);
        }

        // convert the string values into integers
        int[] intVals = new int[numVals];
        for (int i = 0; i < numVals; ++i) {
            int hexBase = 16;
            intVals[i] = Integer.parseInt(byteStrings[i], hexBase);
        }

        return intVals;
    }

    public abstract String tag();
    public abstract void setupDriverMode();
    public abstract void setupNotDriverMode();
    public abstract ValuesCallback getValuesCallback();
}
