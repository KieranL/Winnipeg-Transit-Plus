package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.common.SerializableLatLng
import com.kieran.winnipegbusbackend.common.StopFeature

import java.io.Serializable
import java.util.ArrayList

class StopFeatures(stopNumber: Int, stopName: String, latLng: LatLng?) : Stop(stopName, stopNumber), Serializable {
    private val stopFeatures: ArrayList<StopFeature> = ArrayList()
    private val latLng: SerializableLatLng = SerializableLatLng(latLng)

    fun loadFeatures(stopFeatures: ArrayList<StopFeature>) {
        this.stopFeatures.clear()
        this.stopFeatures.addAll(stopFeatures)
    }

    fun getStopFeatures(): List<StopFeature> {
        return stopFeatures
    }

    fun numberOfFeatures(): Int {
        return stopFeatures.size
    }

    fun getLatLng(): LatLng {
        return latLng.latLng
    }
}
