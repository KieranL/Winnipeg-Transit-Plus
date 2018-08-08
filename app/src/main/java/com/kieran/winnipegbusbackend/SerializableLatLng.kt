package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import java.io.Serializable

class SerializableLatLng(latLng: LatLng) : Serializable {
    val latitude: Double = latLng.latitude
    val longitude: Double = latLng.longitude

    val latLng: LatLng
        get() = LatLng(latitude, longitude)

}

