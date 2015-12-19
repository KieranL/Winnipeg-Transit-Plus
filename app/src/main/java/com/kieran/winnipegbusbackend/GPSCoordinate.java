package com.kieran.winnipegbusbackend;

import java.io.Serializable;

public class GPSCoordinate implements Serializable {
    private double latitude, longitude;

    public GPSCoordinate(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int distanceFrom(GPSCoordinate destination) {

        return 0;
    }

    public String toString() {
        return latitude + " " + longitude;
    }

}
