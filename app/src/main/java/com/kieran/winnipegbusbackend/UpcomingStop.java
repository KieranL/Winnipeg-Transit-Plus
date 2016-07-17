package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

public class UpcomingStop extends Stop implements Comparable {
    private StopTime time;
    private ScheduledStopKey key;

    public UpcomingStop(String stopName, int stopNumber, StopTime time, ScheduledStopKey key) {
        super(stopName, stopNumber);
        this.time = time;
        this.key = key;
    }

    public UpcomingStop(Stop stop, StopTime time, ScheduledStopKey key) {
        super(stop);
        this.time = time;
        this.key = key;
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
