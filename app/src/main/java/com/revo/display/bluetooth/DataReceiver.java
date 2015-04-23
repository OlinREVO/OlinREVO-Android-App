package com.revo.display.bluetooth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by sihrc on 12/10/14.
 */
public class DataReceiver extends BroadcastReceiver {
    ValuesCallback callback;
    
    public DataReceiver(){}
    public DataReceiver(ValuesCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Implement code here to be performed when
        if (callback != null) {
            Log.d("DataReceiver", "revo: " + new String(intent.getByteArrayExtra("revo")));

            // TODO: extract message id
            callback.handleValues(intent.getByteArrayExtra("revo"));
        }
    }
}