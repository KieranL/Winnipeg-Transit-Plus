package com.kieran.winnipegbusbackend;

public class GPSCoordinate {
    private double latitude, longitude;

    public GPSCoordinate(String latitude, String longitude) {
        this.latitude = Double.parseDouble(latitude);
        this.longitude = Double.parseDouble(longitude);
    }


}
