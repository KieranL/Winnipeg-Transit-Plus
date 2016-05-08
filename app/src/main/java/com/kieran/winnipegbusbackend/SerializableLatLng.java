package com.kieran.winnipegbusbackend;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class SerializableLatLng implements Serializable {
    private double latitude, longitude;

    public SerializableLatLng(LatLng latLng) {
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}

