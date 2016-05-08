package com.kieran.winnipegbusbackend;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopFeatures extends Stop implements Serializable {
    private static final String STOP_FEATURE_TAG = "stop-feature";
    private List<StopFeature> stopFeatures;
    private SerializableLatLng latLng;

    public StopFeatures(int stopNumber, String stopName, LatLng latLng) {
        super(stopName, stopNumber);
        this.latLng = new SerializableLatLng(latLng);
        stopFeatures = new ArrayList<>();
    }

    public void loadFeatures(Document document) {
        NodeList features = document.getElementsByTagName(STOP_FEATURE_TAG);
        stopFeatures.clear();

        for(int f = 0; f < features.getLength(); f++)
            stopFeatures.add(new StopFeature(features.item(f)));
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
