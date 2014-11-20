package com.revo.display.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by mwismer on 11/3/14.
 */
// Callback used to get us the results from the server (device)
public class BLEFinderCallback extends BluetoothGattCallback {
    private ArrayList<UUID> mUUIDs = new ArrayList<UUID>();
    private ArrayBlockingQueue<Runnable> infoToGet = new ArrayBlockingQueue<Runnable>(128);
    private boolean success = true;
    private String TAG = "BLEFinderCallback";

    private HashMap<String, byte[]> valueMap = new HashMap<String, byte[]>();

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
        List<BluetoothGattCharacteristic> characteristicList = new ArrayList<BluetoothGattCharacteristic>();
        for (BluetoothGattService service : gatt.getServices()) {
            // List of characteristics in service
            characteristicList.addAll(service.getCharacteristics());
        }

        // Check each characteristic within each service...
        List<BluetoothGattDescriptor> descriptorList = new ArrayList<BluetoothGattDescriptor>();
        for (final BluetoothGattCharacteristic characteristic : characteristicList) {
            if (!mUUIDs.contains(characteristic.getUuid())) { // New characteristic! (not already in our list)
                infoToGet.add(new Runnable() {
                    @Override
                    public void run() {
                        // Request specific characteristic relating to newly discovered or updated data
                        updateSuccess(gatt.readCharacteristic(characteristic));
                    }
                });
                // Add characteristic ID to our list
                mUUIDs.add(characteristic.getUuid());
            }
            // List of descriptors in characteristic
            descriptorList.addAll(characteristic.getDescriptors());
        }


        // Check each descriptor within each characteristic...
        for (final BluetoothGattDescriptor descriptor : descriptorList) {
            if (!mUUIDs.contains(descriptor.getUuid())) { // New descriptor! (not already in our list)
                infoToGet.add(new Runnable() {
                    @Override
                    public void run() {
                        // Request specific descriptor relating to newly discovered or updated data
                        updateSuccess(gatt.readDescriptor(descriptor));
                    }
                });
                // Add descriptor ID to our list
                mUUIDs.add(descriptor.getUuid());
            }
        }

        try {
            infoToGet = new RevoConfig().getServiceWriters(this, gatt, characteristicList, descriptorList, infoToGet);
        } catch (InterruptedException e) {
            Log.d("TAG", "well, shit");
        }
        // Execute read requests before moving on to next service
        readNextBits();

    }

    public void updateSuccess(boolean successful) {
        success = successful;
    }

    // Execute read requests stored in infoToGet, and log the result
    private void readNextBits() {
        while (!infoToGet.isEmpty()) {
            infoToGet.poll().run();
            if (success) {
                break;
            }
        }
        if (infoToGet.isEmpty()) {
            bulkLog();
        }
    }

    // Log all of the new information
    private void bulkLog() {
        Log.d(TAG, "Logging BLE Data: ");
        for (String uuid : valueMap.keySet()) {
            Log.d(TAG, uuid);
            Log.d(TAG, Arrays.toString(valueMap.get(uuid)));
        }
    }

    // Returns response to characteristic read request
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
        valueMap.put(characteristic.getUuid().toString(), characteristic.getValue());

        // Only read next bit of data after current request is completed
        readNextBits();
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);

        readNextBits();
    }

    // Returns response to descriptor read request
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        valueMap.put(descriptor.getUuid().toString(), descriptor.getValue());

        // Only read next bit of data after current request is completed
        readNextBits();
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);

        readNextBits();
    }
}