package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Monument extends Address {
    int key;
    public Monument(JSONObject location) throws JSONException {
        super(location.getJSONObject("address"));

        key = location.getInt("key");
        title = location.getString("name");
    }

    @Override
    public String getURLString() {
        return String.format(Locale.CANADA, "monuments/%d", key);
    }
}
