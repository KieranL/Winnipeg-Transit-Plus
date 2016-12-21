package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class Stop extends Location {
    public Stop(String title) {
        super(title);
    }

    public Stop(JSONObject location) {
        super(location);
    }

    @Override
    public String getURLString() {
        return null;
    }
}
