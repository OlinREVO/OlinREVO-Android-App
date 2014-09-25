package com.revo.display.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Set;

/**
 * Created by sihrc on 9/20/14.
 */
public class Bluetooth {
    final static public int REQUEST_ENABLE_BT = 0;
    BluetoothAdapter mBluetoothAdapter;
    BTConnect connect;
    Context context;
    final BroadcastReceiver mReceiver;

    public Bluetooth(Context context, final DataWatcher dataWatcher) {
        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    Log.i("Bluetooth Discovered Device!", device.getName() + '\n' + device.getAddress());

                    connect = new BTConnect(device, mBluetoothAdapter, dataWatcher);
                }
            }
        };
        this.context = context;
        registerBTReceiver();
    }

    /**
     * Nuts and Bolts for Getting a Bluetooth Connection
     */

    private void startDiscovery() {
        //Start Bluetooth Discovery
        mBluetoothAdapter.startDiscovery();
    }

    /** Initializing Bluetooth **/
    public void initializeBTAdapter(Activity activity) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.e("Bluetooth Error", "Device does not support Bluetooth");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.i("Bluetooth Found Device!", device.getName() + '\n' + device.getAddress());
            }
        }

        startDiscovery();
    }

    private void registerBTReceiver() {
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }
}
