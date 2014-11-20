package com.revo.display;

import android.app.Application;

import com.revo.display.network.RFirebase;

/**
 * Created by sihrc on 9/24/14.
 */
public class RevoApplication extends Application {
    public static RevoApplication app;
    RFirebase fb;

    // Request Code for Bluetooth
    final public static int REQUEST_ENABLE_BT = 100;
    // Timeout for Bluetooth Scanning
    final public static long SCAN_PERIOD = 10000;

    @Override
    public void onCreate() {
        super.onCreate();

        //Grab Static Reference
        app = this;
        fb = new RFirebase(this);
    }


    public RFirebase getFireBaseHelper() {
        return fb;
    }
}