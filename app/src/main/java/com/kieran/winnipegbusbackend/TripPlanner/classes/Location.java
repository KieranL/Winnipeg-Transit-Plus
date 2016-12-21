package com.kieran.winnipegbusbackend.TripPlanner.classes;

import org.json.JSONObject;

import java.io.Serializable;

public abstract class Location implements Serializable {
    String title;

    public Location(String title) {
        this.title = title;
    }

    public Location(JSONObject location) {

    }

    public abstract String getURLString();

    public String getTitle() {
        return title;
    }
}
