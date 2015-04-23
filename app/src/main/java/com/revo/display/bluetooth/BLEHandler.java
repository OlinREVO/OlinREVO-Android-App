package com.revo.display.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mwismer on 11/3/14.
 * Refactored by sihrc on 11/19/14
 */
// Callback used to get us the results from the server (device)
public class BLEHandler extends BluetoothGattCallback {
    final private static String TAG = "BLEFinderCallback";

    BLEActivity activity;

    public void setActivity(BLEActivity activity) {
        this.activity = activity;
    }

    // Client connection state has changed
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            Log.i(TAG, "Connected to GATT server.");
            Log.i(TAG, "Attempting to start service discovery:" + gatt.discoverServices());

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            Log.i(TAG, "Disconnected from GATT server.");
        }
    }

    // Server's list of (remote services, characteristics and descriptors) for the remote device (server) has been updated
    @Override
    public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i("BLEHandler", "Service discovery completed!");
        }
        else {
            Log.i("BLEHandler", "Service discovery failed with status: " + status);
        }
        // Save reference to each characteristic.
//        tx = gatt.getService(UUID.fromString(GattAttributes.SERVICE)).getCharacteristic(TX_UUID);
        BluetoothGattService service = gatt.getService(GattAttributes.SERVICE);
        BluetoothGattCharacteristic rx = service.getCharacteristic(GattAttributes.CLIENT_CHARACTERISTIC_READ);
        // Setup notifications on RX characteristic changes (i.e. data received).
        // First call setCharacteristicNotification to enable notification.
        if (!gatt.setCharacteristicNotification(rx, true)) {
            Log.i("BLEHandler", "Couldn't set notifications for RX characteristic!");
        }
        // Next update the RX characteristic's client descriptor to enable notifications.
        if (rx.getDescriptor(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG) != null) {
            BluetoothGattDescriptor desc = rx.getDescriptor(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG);
            desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            if (!gatt.writeDescriptor(desc)) {
                Log.i("BLEHandler", "Couldn't write RX client descriptor value!");
            }
        }
        else {
            Log.i("BLEHandler", "Couldn't get RX client descriptor!");
        }
    }

    // Returns response to characteristic read request
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        String key = characteristic.getUuid().toString();
        byte[] values = characteristic.getValue();

        Log.i("BLEFinderCallback " + key, "Characteristic Read: " + Arrays.toString(values));

    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }


    // Returns response to descriptor read request
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        String key = descriptor.getUuid().toString();
        byte[] values = descriptor.getValue();

        Log.i("BLEFinderCallback " + key, "Descriptor Notify: " + Arrays.toString(values));
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);

        String key = characteristic.getUuid().toString();
        byte[] values = characteristic.getValue();

        broadcastUpdate(characteristic);
        Log.i("BLEFinderCallback " + key, "Characteristic Notify: " + Arrays.toString(values));
    }

    private void broadcastUpdate(BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent("REVO_APP_DISPLAY");
        byte[] values = characteristic.getValue();
        Log.d(TAG, "UUID: " + characteristic.getUuid());
        //intent.putExtra("revo", String.valueOf(values));
        intent.putExtra("revo", values);

        activity.sendBroadcast(intent);
    }
}