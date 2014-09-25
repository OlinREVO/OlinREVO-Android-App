package com.revo.display.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * Created by sihrc on 9/20/14.
 */
public class BTConnect extends Thread {
    private BluetoothAdapter mBluetoothAdapter;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    DataWatcher watcher;
    boolean keepListening = true;

    final UUID MY_UUID = new UUID(13131313, 131313); //FIXME - HARDCODED RANDOM UUID

    public BTConnect(BluetoothDevice device, BluetoothAdapter mBlueToothAdapter, DataWatcher dataWatcher) {
        //Grab required references
        this.mBluetoothAdapter = mBlueToothAdapter;
        this.watcher = new DataWatcher();

        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException ignored) {
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();

            new Thread(){
                        BufferedReader reader;
                        char[] data = new char[1024];
                        @Override
                        public void run() {
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
                                while (keepListening){
                                    reader.read(data, 0, 1024);
                                    watcher.read(data);
                                }
                            } catch ( IOException e ) {
                                close( reader );
                            }
                        }
                    }.start();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException ignored) {}
            return;
        }

    }

    /**
     * Will cancel an in-progress connection, and close the socket
     */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException ignored) {
        }
    }

    private static void close(Closeable aConnectedObject) {
        if ( aConnectedObject == null ) return;
        try {
            aConnectedObject.close();
        } catch ( IOException ignored) {
        }
    }
}