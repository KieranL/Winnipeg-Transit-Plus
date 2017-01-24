package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Locale;

public class Location implements Serializable {
    LatLng point;
    String title;

    public Location(JSONObject location) {
        getLatLngFromLocation(location);
        title = "Location";
    }

    public Location(JSONObject location, String title) {
        getLatLngFromLocation(location);
        this.title = title;
    }

    private void getLatLngFromLocation(JSONObject location) {
        try{
            JSONObject centre = location.getJSONObject("centre");
            JSONObject geographic = centre.getJSONObject("geographic");
            point = new LatLng(geographic.getDouble("latitude"), geographic.getDouble("longitude"));
        }catch (JSONException e) {

        }
    }

    public String getURLString() {
        return String.format(Locale.CANADA, "geo/%f,%f", point.latitude, point.longitude);
    }

    public String getTitle() {
        return title;
    }
}
