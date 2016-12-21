package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class Intersection extends Location {
    public Intersection(String title) {
        super(title);
    }

    public Intersection(JSONObject location) {
        super(location);
    }

    @Override
    public String getURLString() {
        return null;
    }
}
