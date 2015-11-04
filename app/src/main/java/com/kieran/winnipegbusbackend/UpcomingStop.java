package com.kieran.winnipegbusbackend;

public class UpcomingStop {
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
}
