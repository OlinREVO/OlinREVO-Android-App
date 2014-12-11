package com.revo.display;

import android.app.Application;

import com.revo.display.network.RFirebase;

/**
 * Created by sihrc on 9/24/14.
 */
public class RevoApplication extends Application {
    public static RevoApplication app;
    public static boolean isDriver = false;

    RFirebase fb;

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