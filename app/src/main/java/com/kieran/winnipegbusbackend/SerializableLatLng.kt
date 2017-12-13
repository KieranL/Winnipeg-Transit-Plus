package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import java.io.Serializable

class SerializableLatLng(latLng: LatLng) : Serializable {
    private val latitude: Double = latLng.latitude
    private val longitude: Double = latLng.longitude

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

}

