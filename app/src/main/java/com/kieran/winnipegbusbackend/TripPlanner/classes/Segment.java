package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Segment {
    Times times;
    Location from;
    Location to;

    public Segment(JSONObject object, String fromTitle, String toTitle) {
        try {
            JSONObject times = object.getJSONObject("times");
            this.times = new Times(times);

            from = LocationFactory.createLocation(object.getJSONObject("from"), fromTitle);
            to = LocationFactory.createLocation(object.getJSONObject("to"), toTitle);

        }catch (JSONException ex) {

        }
    }

    @Override
    public String toString() {
        return from.getTitle() + " to " + to.getTitle();
    }

    public Times getTimes() {
        return times;
    }
}
