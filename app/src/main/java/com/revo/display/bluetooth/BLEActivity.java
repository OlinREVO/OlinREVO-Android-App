package com.revo.display.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * Created by mwismer on 11/3/14.
 * Refactored by sihrc on 11/19/14
 */
public class BLEActivity extends Activity {
    public final static int ENABLE_BLE = 21305;
    //Log TAG
    private final static String TAG = "REVO::BLEHandler";
    //Scanning Timer
    private Handler mHandler = new Handler();
    //BLE Handler
    BLEHandler handler = new BLEHandler();

    final private static long SCAN_PERIOD = 10000; //Time to scan in ms


    //Bluetooth Device
    private String deviceName;
    private BluetoothDevice device;
    private BluetoothAdapter mBLEAdapter;

    // BLE Callback
    private BluetoothAdapter.LeScanCallback mBLECallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            Log.d(TAG, "bluetooth device: " + bluetoothDevice + ", " + i + ", " + bytes);
            if (device == null && bluetoothDevice.getAddress().equals(deviceName)) {
                Log.d(TAG, "Setting device");
                device = bluetoothDevice;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLE) {
            Log.d(TAG, "BLE Enabled. Trying to scan again");
            scanBLE(deviceName);
        }
    }

    public void scanBLE(String nameOfDevice) {
        handler.setActivity(this);
        deviceName = nameOfDevice;
        startScan(checkBLEEnabled());
    }

    public boolean checkBLEEnabled() {
        mBLEAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        return (mBLEAdapter != null && mBLEAdapter.isEnabled());
    }

    private void startScan(final boolean enable) {
        Log.d(TAG, "starting scan");
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBLEAdapter.stopLeScan(mBLECallback);
                    if (device != null) {
                        device.connectGatt(BLEActivity.this, false, handler);
                    }
                }
            }, SCAN_PERIOD);

            mBLEAdapter.startLeScan(mBLECallback);
        } else {
            Log.d(TAG, "BLE not enabled");
            mBLEAdapter.stopLeScan(mBLECallback);
            enableBLE();
        }
    }

    public void enableBLE() {
        Log.d(TAG, "Enabling Bluetooth");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, ENABLE_BLE);
    }
}
