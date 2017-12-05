package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import java.io.Serializable

class SerializableLatLng(latLng: LatLng) : Serializable {
    private val latitude: Double
    private val longitude: Double

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

    init {
        latitude = latLng.latitude
        longitude = latLng.longitude
    }
}

