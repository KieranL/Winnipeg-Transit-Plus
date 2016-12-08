package com.kieran.winnipegbusbackend.TripPlanner;

import com.kieran.winnipegbusbackend.TripPlanner.classes.RideSegment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.TransferSegment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.WalkSegment;

import org.json.JSONException;
import org.json.JSONObject;

public class SegmentFactory {
    public static Segment createSegment(JSONObject segment) {
        try {
            switch (segment.getString("type")) {
                default: return null;
                case "walk": return new WalkSegment();
                case "ride": return new RideSegment();
                case "transfer": return new TransferSegment();

            }
        } catch (JSONException e) {
            return null;
        }
    }
}
