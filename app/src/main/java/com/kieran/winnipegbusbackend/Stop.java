package com.kieran.winnipegbusbackend;

import java.io.Serializable;

public class Stop implements Serializable {
    public final static String STOP_NAME_TAG = "name";
    public final static String STOP_NUMBER_TAG = "number";
    public final static String STOP_TAG = "stop";
    protected String stopName;
    protected int stopNumber;

    public Stop() {
        stopName = "";
        stopNumber = 0;
    }

    public Stop(String stopName, int stopNumber) {
        this.stopName = stopName;
        this.stopNumber = stopNumber;
    }

    public Stop(int stopNumber) {
        this.stopNumber = stopNumber;
        stopName = "";
    }

    public Stop(Stop stop) {
        stopName = stop.getName();
        stopNumber = stop.getNumber();
    }

    public String getName() {
        return stopName;
    }

    public int getNumber() {
        return stopNumber;
    }

    @Override
    public String toString() {
        return Integer.toString(stopNumber) + " " + stopName;
    }
}
