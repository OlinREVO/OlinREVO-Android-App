package com.revo.display.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.revo.display.network.ValueCallback;

import java.util.ArrayList;

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

    private ArrayList<ValueCallback> listeners;

    private Context context;

    public OrientationSensor(Context context) {
        this.context = context;

        listeners = new ArrayList<ValueCallback>();

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

    public void registerListener(ValueCallback listener) {
        listeners.add(listener);
    }

    public void unregisterListeners() {
        listeners.clear();
    }

    private void notifyListeners() {
        for (ValueCallback listener : listeners) {
            listener.handleValue((long) direction);
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
                float directionInRadians = -orientation[0];
                float directionInDegrees = (float) Math.toDegrees(directionInRadians);
                direction = (directionInDegrees + 270) % 360;
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
