package com.revo.display.network;

import com.firebase.client.DataSnapshot;

/**
 * Created by sihrc on 9/24/14.
 */
public interface SnapshotCallback {
    public void handleSnapshot(DataSnapshot snapshot);
}
