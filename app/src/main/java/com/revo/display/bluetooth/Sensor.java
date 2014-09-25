package com.revo.display.bluetooth;

/**
 * Created by sihrc on 9/24/14.
 */
public enum Sensor {
    SPEED(0, 1);

    int begin, size;
    private Sensor(int begin, int size) {
        this.begin = begin;
        this.size = size;
    }
}
