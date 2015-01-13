package com.revo.display.sensors;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by isaac on 10/24/14.
 */
public class OrientationSensor implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnometer;

    private float[] acceleration;
    private float[] magneticField;
    private float direction;

    private ArrayList<OrientationChangeListener> listeners;

    private Context context;

    public OrientationSensor(Context context) {
        this.context = context;

        listeners = new ArrayList<OrientationChangeListener>();

        // get the sensors
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // listen to the sensors
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(this, magnometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void unregisterSensors() {
        sensorManager.unregisterListener(this);
    }

    public float getDirection() {
        return direction;
    }

    //
    // setup listeners
    //

    public void registerListener(OrientationChangeListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(OrientationChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (OrientationChangeListener listener : listeners) {
            listener.onDirectionChange(direction);
        }
    }

    //
    // handle events
    //

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            acceleration = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            magneticField = event.values;
        if (acceleration != null && magneticField != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, acceleration, magneticField);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                direction = (float) (-orientation[0] * (360 / (2 * Math.PI)));
            }
        }
        notifyListeners();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

}
