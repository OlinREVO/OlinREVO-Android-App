package com.revo.display;

import android.app.Application;

import com.revo.display.bluetooth.Bluetooth;
import com.revo.display.bluetooth.ChangeListener;
import com.revo.display.bluetooth.DataWatcher;
import com.revo.display.network.FirebaseHelper;

/**
 * Created by sihrc on 9/24/14.
 */
public class RevoApplication extends Application {
    public static RevoApplication app;
    FirebaseHelper fb;
    Bluetooth bluetooth;
    DataWatcher watcher;

    @Override
    public void onCreate() {
        super.onCreate();

        //Grab Static Reference
        app = this;
        fb = new FirebaseHelper(this);
        watcher = new DataWatcher();
        bluetooth = new Bluetooth(this, watcher);
    }

    public Bluetooth getBluetoothConnection() {
        return bluetooth;
    }

    public FirebaseHelper getFireBaseHelper() {
        return fb;
    }

    public void addDataListener(String id, ChangeListener listener) {
        watcher.addListener(id, listener);
    }
}
