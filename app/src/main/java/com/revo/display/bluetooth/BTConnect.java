package com.revo.display.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by sihrc on 9/20/14.
 */
public class BTConnect extends Thread {
    private BluetoothAdapter mBluetoothAdapter;
    private SocketCallback callback;
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;

    final UUID MY_UUID = new UUID(13131313, 131313); //FIXME - HARDCODED RANDOM UUID

    public BTConnect(BluetoothDevice device, BluetoothAdapter mBlueToothAdapter, SocketCallback callback) {
        //Grab required references
        this.mBluetoothAdapter = mBlueToothAdapter;
        this.callback = callback;

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
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) {
            }
            return;
        }

        // Do work to manage the connection (in a separate thread)
        callback.receivedSocket(mmSocket);
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
}