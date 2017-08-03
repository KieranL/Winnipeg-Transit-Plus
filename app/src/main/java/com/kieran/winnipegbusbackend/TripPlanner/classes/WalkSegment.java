package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class WalkSegment extends Segment {
    public WalkSegment(JSONObject segment) {
        super(segment);
    }

    @Override
    public String toString() {
        if(from != null && to != null)
            return super.toString();
        else
            return "Walk";
    }
}
