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
            if(segment.has("origin"))
                segment = segment.getJSONObject("origin");
            else if(segment.has("destination"))
                segment = segment.getJSONObject("destination");

            switch (segment.getString("type")) {
                default: return null;
                case "walk": return new WalkSegment(segment);
                case "ride": return new RideSegment(segment);
                case "transfer": return new TransferSegment(segment);
            }
        } catch (JSONException e) {
            return null;
        }
    }
}
