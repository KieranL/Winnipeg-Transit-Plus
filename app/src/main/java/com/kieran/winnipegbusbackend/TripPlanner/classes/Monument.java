package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class Monument extends Location {
    public Monument(String title) {
        super(title);
    }

    public Monument(JSONObject location) {
        super(location);
    }

    @Override
    public String getURLString() {
        return null;
    }
}
