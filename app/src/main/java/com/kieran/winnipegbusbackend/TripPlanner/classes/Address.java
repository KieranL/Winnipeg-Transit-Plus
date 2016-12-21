package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

public class Address extends Location {
    public Address(String title) {
        super(title);
    }

    public Address(JSONObject location) {
        super(location);
    }

    @Override
    public String getURLString() {
        return null;
    }
}
