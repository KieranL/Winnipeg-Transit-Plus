package com.kieran.winnipegbusbackend;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopFeatures extends Stop implements Serializable {
    private static final String STOP_FEATURE_TAG = "stop-features";
    private List<StopFeature> stopFeatures;
    private SerializableLatLng latLng;

    public StopFeatures(int stopNumber, String stopName, LatLng latLng) {
        super(stopName, stopNumber);
        this.latLng = new SerializableLatLng(latLng);
        stopFeatures = new ArrayList<>();
    }

    public void loadFeatures(JSONObject document) {
        try {
            JSONArray features = document.getJSONArray(STOP_FEATURE_TAG);
            stopFeatures.clear();

            for(int f = 0; f < features.length(); f++)
                stopFeatures.add(new StopFeature(features.getJSONObject(f)));
        } catch (JSONException e) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public List<StopFeature> getStopFeatures() {
        return stopFeatures;
    }

    public int numberOfFeatures() {
        return stopFeatures.size();
    }

    public LatLng getLatLng() {
        return latLng.getLatLng();
    }
}
