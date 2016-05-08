package com.kieran.winnipegbusbackend;

import java.io.Serializable;

public class ScheduledStopKey implements Serializable{
    private int busKey;
    private int stopNumber;

    public ScheduledStopKey(String key) {
        String[] keys = key.split("-");
        //substring is needed for unexpected characters in some keys, i.e. some keys for Route 75: 7191450NNYNYNN-2
        busKey = Integer.parseInt(keys[0].substring(0, 7));
        stopNumber = Integer.parseInt(keys[1]);
    }

    public int getBusKey() {
        return busKey;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public String toString() {
        return "Key: " + busKey + " stop on route: " + stopNumber;
    }

    public boolean equals(ScheduledStopKey another) {
        return busKey == another.getBusKey();
    }
}
