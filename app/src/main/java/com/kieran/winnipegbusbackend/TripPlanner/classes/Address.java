package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Address extends Location {
    int key;
    int streetNumber;
    String streetName;

    public Address(JSONObject location) {
        super(location);

        try {
            JSONObject street = location.getJSONObject("street");

            key = location.getInt("key");
            streetNumber = location.getInt("street-number");
            streetName = street.getString("name");
            title = String.format(Locale.CANADA, "%d %s", streetNumber, streetName);
        } catch (JSONException e) {

        }
    }

    @Override
    public String getURLString() {
        return String.format(Locale.CANADA, "addresses/%d", key);
    }
}
