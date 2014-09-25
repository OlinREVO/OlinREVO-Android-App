package com.revo.display.bluetooth;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sihrc on 9/24/14.
 */
public class DataWatcher {

    Map<String, ChangeListener> listeners;
    public DataWatcher() {
        listeners = new HashMap<String, ChangeListener>();
    }

    public void read(char[] data) {
        for (Sensor sensor : Sensor.values()) {

        }
    }


    public void addListener(String id, ChangeListener listener) {
        listeners.put(id, listener);
    }

    public void removeListener(String id) {
        listeners.remove(id);
    }

    private class DataHolder {

    }
}
