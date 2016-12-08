package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;

import java.io.Serializable;

public class TripParameters implements Serializable{
    Location origin;
    Location destination;
    StopTime time;

    public TripParameters() {
        time = new StopTime();
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public StopTime getTime() {
        return time;
    }

    public void setTime(StopTime time) {
        this.time = time;
    }

    public String getURL() {
        return "https://api.winnipegtransit.com/v2/trip-planner.json?origin=addresses/136590&destination=intersections/123172:378@954&api-key=FTy2QN8ts293ZlhYP1t";
    }
}
