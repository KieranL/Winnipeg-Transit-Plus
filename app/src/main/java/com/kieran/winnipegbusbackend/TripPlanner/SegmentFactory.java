package com.kieran.winnipegbusbackend.TripPlanner;

import com.kieran.winnipegbusbackend.TripPlanner.classes.RideSegment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.TransferSegment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.WalkSegment;

import org.json.JSONException;
import org.json.JSONObject;

public class SegmentFactory {
    public static Segment createSegment(JSONObject segment, String fromTitle, String toTitle) {
        try {
            switch (segment.getString("type")) {
                default: return null;
                case "walk": return new WalkSegment(segment, fromTitle, toTitle);
                case "ride": return new RideSegment(segment, fromTitle, toTitle);
                case "transfer": return new TransferSegment(segment, fromTitle, toTitle);
            }
        } catch (JSONException e) {
            return null;
        }
    }
}
