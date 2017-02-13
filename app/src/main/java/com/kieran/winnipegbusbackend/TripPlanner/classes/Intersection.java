package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Intersection extends Location {
    String key;
    String streetName;
    String crossStreetName;

    public Intersection(JSONObject location) {
        super(location);
        try {
            JSONObject street = location.getJSONObject("street");
            JSONObject crossStreet = location.getJSONObject("cross-street");

            key = location.getString("key");
            streetName = street.getString("name");
            crossStreetName = crossStreet.getString("name");
            title = String.format(Locale.CANADA, "%s at %s", streetName, crossStreetName);
        } catch (JSONException e) {

        }

    }

    @Override
    public String getURLString() {
        return String.format(Locale.CANADA, "intersections/%s", key);
    }
}
