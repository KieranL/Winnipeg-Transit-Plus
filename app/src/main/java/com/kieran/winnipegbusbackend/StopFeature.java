package com.kieran.winnipegbusbackend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class StopFeature implements Serializable {
    private static final String NAME_TAG = "name";
    private static final String COUNT_TAG = "count";
    private int count;
    private String name;

    public StopFeature(int count, String name) {
        this.count = count;
        this.name = name;
    }

    public StopFeature(JSONObject featureNode) {
        try {
            name = featureNode.getString(NAME_TAG);
            count = featureNode.getInt(COUNT_TAG);
        } catch (JSONException e) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
