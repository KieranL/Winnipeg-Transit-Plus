package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Stop extends Location {
    int stopNumber;

    public Stop(JSONObject location) {
        super(location);

        try {
            stopNumber = location.getInt("key");
            title = location.getString("name");
        } catch (JSONException e) {

        }
    }
}
