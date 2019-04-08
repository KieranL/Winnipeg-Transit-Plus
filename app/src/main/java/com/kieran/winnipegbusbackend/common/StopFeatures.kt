package com.kieran.winnipegbusbackend.common

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.common.SerializableLatLng
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.common.StopFeature
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

import java.io.Serializable
import java.util.ArrayList

class StopFeatures(stopIdentifier: StopIdentifier, stopName: String, latLng: LatLng?) : Stop(stopName, stopIdentifier), Serializable {
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
