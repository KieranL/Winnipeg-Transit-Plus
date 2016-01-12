package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

public class UpcomingStop implements Comparable {
    private String stopName;
    private StopTime time;
    private int stopNumber;
    private ScheduledStopKey key;

    public UpcomingStop(String stopName, int stopNumber, StopTime time, ScheduledStopKey key) {
        this.stopName = stopName;
        this.stopNumber = stopNumber;
        this.time = time;
        this.key = key;
    }

    public String getStopName() {
        return stopName;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public StopTime getTime() {
        return time;
    }

    public ScheduledStopKey getKey() {
        return key;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return key.getStopNumber() - ((UpcomingStop)another).getKey().getStopNumber();
    }
}
