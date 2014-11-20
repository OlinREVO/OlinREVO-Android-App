package com.revo.display;

import android.app.Application;

import com.revo.display.network.RFirebase;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by sihrc on 9/24/14.
 */
public class RevoApplication extends Application {
    public static RevoApplication app;
    RFirebase fb;

    final static private HashMap<UUID, Byte> BLE_UUID_MAP = new HashMap<UUID, Byte>(8) {{
        put(UUID.fromString("f000aa02-0451-4000-b000-000000000000"), (byte) 1); //IRT
        put(UUID.fromString("f000aa12-0451-4000-b000-000000000000"), (byte) 3); //ACC: weird enable
        put(UUID.fromString("f000aa22-0451-4000-b000-000000000000"), (byte) 1); //HUM
        put(UUID.fromString("f000aa32-0451-4000-b000-000000000000"), (byte) 1); //MAG
        put(UUID.fromString("f000aa42-0451-4000-b000-000000000000"), (byte) 1); //BAR
        put(UUID.fromString("f000aa52-0451-4000-b000-000000000000"), (byte) 7); //GYRO: weird enable
    }};

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