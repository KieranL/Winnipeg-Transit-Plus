package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;

import org.json.JSONObject;

public abstract class Segment {
    StopTime start;
    StopTime end;
    Location from;
    Location to;

    public Segment(JSONObject object) {

    }
}
