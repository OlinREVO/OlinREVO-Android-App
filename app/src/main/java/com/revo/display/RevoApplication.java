package com.revo.display;

import android.app.Application;

import com.revo.display.network.FirebaseHelper;

/**
 * Created by sihrc on 9/24/14.
 */
public class RevoApplication extends Application {
    public static RevoApplication app;
    FirebaseHelper fb;

    @Override
    public void onCreate() {
        super.onCreate();

        //Grab Static Reference
        app = this;
        fb = new FirebaseHelper(this);
    }


    public FirebaseHelper getFireBaseHelper() {
        return fb;
    }
}