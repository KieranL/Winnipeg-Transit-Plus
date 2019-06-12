package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import java.io.Serializable

class StopFeatures(stopIdentifier: StopIdentifier, stopName: String, latLng: GeoLocation?) : Stop(stopName, stopIdentifier, latLng), Serializable {
    private val stopFeatures: ArrayList<StopFeature> = ArrayList()

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
}
