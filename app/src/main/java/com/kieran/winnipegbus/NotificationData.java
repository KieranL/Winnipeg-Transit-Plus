package com.kieran.winnipegbus;

import android.content.Context;

import com.kieran.winnipegbusbackend.ScheduledStopKey;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import java.io.Serializable;

public class NotificationData extends Stop implements Serializable{
    private int routeNumber;
    private ScheduledStopKey key;
    private String variantName;
    private transient Context context;
    private StopTime startTime;
    private CoverageTypes coverageType;

    public NotificationData(int stopNumber, int routeNumber, ScheduledStopKey key, String variantName, String stopName, Context context, StopTime startTime, CoverageTypes coverageType) {
        super(stopName, stopNumber);
        this.routeNumber = routeNumber;
        this.key = key;
        this.variantName = variantName;
        this.context = context;
        this.startTime = startTime;
        this.coverageType = coverageType;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public ScheduledStopKey getKey() {
        return key;
    }

    public String getVariantName() {
        return variantName;
    }

    public Context getContext() {
        return context;
    }

    public StopTime getStartTime() {
        return startTime;
    }

    public void setStartTime(StopTime startTime) {
        this.startTime = startTime;
    }

    public CoverageTypes getCoverageType() {
        return coverageType;
    }
}
