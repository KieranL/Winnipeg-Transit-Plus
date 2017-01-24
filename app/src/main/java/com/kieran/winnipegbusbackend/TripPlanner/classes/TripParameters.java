package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TripPlanner.TimeMode;

import java.io.Serializable;
import java.util.Locale;

public class TripParameters implements Serializable{
    Location origin;
    Location destination;
    StopTime time;
    TimeMode timeMode;

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
//        return "https://api.winnipegtransit.com/v2/trip-planner.json?origin=addresses/136590&destination=intersections/123172:378@954&api-key=FTy2QN8ts293ZlhYP1t";
        return String.format(Locale.CANADA, "https://api.winnipegtransit.com/v2/trip-planner.json?origin=%s&destination=%s&mode=%s&time=%s&api-key=FTy2QN8ts293ZlhYP1t", origin.getURLString(), destination.getURLString(), timeMode.urlParameter, time.toURLTimeString());
    }
}
