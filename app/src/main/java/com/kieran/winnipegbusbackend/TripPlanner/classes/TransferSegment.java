package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class TransferSegment extends Segment {
    public TransferSegment(JSONObject segment, String fromTitle, String toTitle) {
        super(segment, fromTitle, toTitle);
    }
}
