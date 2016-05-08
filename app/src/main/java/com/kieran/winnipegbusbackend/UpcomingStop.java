package com.kieran.winnipegbusbackend;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kieran.winnipegbus.NotificationData;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

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

    public NotificationData toNotificationData(int routeNumber, String variantName, Context context, CoverageTypes coverageType) {
        return new NotificationData(stopNumber, routeNumber, key, variantName, stopName, context, time, coverageType);
    }

}
