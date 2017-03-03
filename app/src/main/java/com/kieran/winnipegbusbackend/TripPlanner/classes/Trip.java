package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.TripPlanner.SegmentFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Trip {
    private Times times;
    ArrayList<Segment> segments;

    public Trip(JSONObject trip) {
        segments = new ArrayList<>();
        getSegments(trip);
    }

    private void getSegments(JSONObject trip) {
        try {
            JSONObject times = trip.getJSONObject("times");
            this.times = new Times(times);
            JSONArray segmentNodes = trip.getJSONArray("segments");

            for(int i = 0; i < segmentNodes.length(); i++) {
                JSONObject segment = segmentNodes.getJSONObject(i);
                segments.add(SegmentFactory.createSegment(segment));
            }



        } catch (JSONException e) {

        }
    }


    public ArrayList<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.get(0).times.startTime.to12hrTimeString() + "-" + segments.get(segments.size() - 1).times.endTime.to12hrTimeString();
    }

    public Times getTimes() {
        return times;
    }
}
