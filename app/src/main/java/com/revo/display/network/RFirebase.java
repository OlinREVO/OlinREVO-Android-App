package com.revo.display.network;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sihrc on 9/24/14.
 */
public class RFirebase {
    final static private String DB_URL = "https://revodisplay.firebaseio.com/";

    Context context;
    Firebase firebase;
    Map<String, ValueEventListener> eventListenerMap = new HashMap<String, ValueEventListener>();

    public RFirebase(Context context) {
        this.context = context;
        this.firebase = new Firebase(DB_URL);
    }

    public void write(String key, String value) {
        firebase.child(key).setValue(value);
    }

    public void registerListener(final String TAG, final String[] keys, final ValueCallback callback) {
        if (TAG == null || keys == null || callback == null)
            return;

        Firebase ref = getRef(keys);
        if (eventListenerMap.containsKey(TAG)) {
            ref.addValueEventListener(eventListenerMap.get(TAG));
            return;
        }

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i("Firebase Value Event Listener", "Received snapshot for " + Arrays.toString(keys));
                callback.handleValue((Long) snapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase Value Event Listener", "Firebase Error occurred when retrieving" + Arrays.toString(keys));
            }
        };

        eventListenerMap.put(TAG, listener);
        ref.addValueEventListener(listener);
    }

    public void deregisterListener(String TAG, final String[] keys) {
        if (keys == null || TAG == null || !eventListenerMap.containsKey(TAG))
            return;

        Firebase ref = getRef(keys);

        ref.removeEventListener(eventListenerMap.get(TAG));
    }

    public Firebase getRef(String[] keys) {
        Firebase ref = firebase;

        for (String key : keys)
            ref = ref.child(key);

        return ref;
    }
}