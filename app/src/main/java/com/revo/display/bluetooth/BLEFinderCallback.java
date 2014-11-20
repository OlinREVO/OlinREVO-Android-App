package com.revo.display.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by mwismer on 11/3/14.
 */
// Callback used to get us the results from the server (device)
public class BLEFinderCallback extends BluetoothGattCallback {
    final private static String TAG = "BLEFinderCallback";

    private Set<UUID> mUUIDs = new HashSet<UUID>();

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

        // Check each service within device...
        for (BluetoothGattService service : gatt.getServices()) {
            // Check each characteristic within each service...
            for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                if (!mUUIDs.contains(characteristic.getUuid())) { // New characteristic! (not already in our list)
                    // Read the Characteristic
                    gatt.readCharacteristic(characteristic);
                    // Add characteristic ID to our list
                    mUUIDs.add(characteristic.getUuid());
                }
                // List of descriptors in characteristic
                for (final BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                    if (!mUUIDs.contains(descriptor.getUuid())) { // New descriptor! (not already in our list)
                        // Read the Descriptor
                        gatt.readDescriptor(descriptor);
                        // Add descriptor ID to our list
                        mUUIDs.add(descriptor.getUuid());
                    }
                }
            }
        }
    }

    // Returns response to characteristic read request
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        String key = characteristic.getUuid().toString();
        byte[] vales = characteristic.getValue();
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
        byte[] vales = descriptor.getValue();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
    }
}