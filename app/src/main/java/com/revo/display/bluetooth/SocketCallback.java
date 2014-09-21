package com.revo.display.bluetooth;

import android.bluetooth.BluetoothSocket;

/**
 * Created by sihrc on 9/20/14.
 */
public interface SocketCallback {
    void receivedSocket(BluetoothSocket socket);
}
