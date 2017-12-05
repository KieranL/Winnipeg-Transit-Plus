package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class WalkSegment extends Segment {
    public WalkSegment(JSONObject segment, String fromTitle, String toTitle) {
        super(segment, fromTitle, toTitle);
    }

    @Override
    public String toString() {
        if(from != null && to != null)
            return super.toString();
        else
            return "Walk";
    }
}
