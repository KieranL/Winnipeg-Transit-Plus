package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class Coordinate extends Location {
    public Coordinate(String title) {
        super(title);
    }

    public Coordinate(JSONObject location) {
        super(location);
    }

    @Override
    public String getURLString() {
        return null;
    }
}
