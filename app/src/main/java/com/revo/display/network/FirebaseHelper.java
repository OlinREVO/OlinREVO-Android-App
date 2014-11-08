package com.revo.display.network;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by sihrc on 9/24/14.
 */
public class FirebaseHelper {
    final static private String DB_URL = "https://revodisplay.firebaseio.com/";

    Context context;
    Firebase firebase;

    public FirebaseHelper(Context context) {
        this.context = context;
        this.firebase = new Firebase(DB_URL);
    }

    public <T> void write(String key, T value) {
        firebase.child(key).setValue(value);
    }

    public void registerReader(final String key, final SnapshotCallback callback) {
        Log.i("Firebase Value Event", "Registered Listener for " + key);
        firebase.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i("Firebase Value Event Listener", "Received snapshot for " + key);
                callback.handleSnapshot(snapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e("Firebase Value Event Listener", "Firebase Error occurred when retrieving" + key);
            }
        });
    }
}
