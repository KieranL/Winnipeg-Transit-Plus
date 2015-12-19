package com.kieran.winnipegbusbackend;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StopFeatures implements Serializable {
    private static final String STOP_FEATURE = "stop-feature";
    private int stopNumber;
    private String stopName;
    private List<StopFeature> stopFeatures;
    private GPSCoordinate coordinates;

    public StopFeatures(int stopNumber, String stopName, GPSCoordinate coordinates) {
        this.stopNumber = stopNumber;
        this.stopName = stopName;
        this.coordinates = coordinates;
        stopFeatures = new ArrayList<>();
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public String getStopName() {
        return stopName;
    }

    public void loadFeatures(Document document) {
        NodeList features = document.getElementsByTagName(STOP_FEATURE);

        for(int f = 0; f < features.getLength(); f++)
            stopFeatures.add(new StopFeature(features.item(f)));
    }

    public List<StopFeature> getStopFeatures() {
        return stopFeatures;
    }

    public GPSCoordinate getCoordinates() {
        return coordinates;
    }

    public int getNumberofFeatures() {
        return stopFeatures.size();
    }

    public void clearStopFeatures() {
        stopFeatures.clear();
    }
}
