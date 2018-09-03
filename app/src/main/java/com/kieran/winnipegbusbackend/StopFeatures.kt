package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList

class StopFeatures(stopNumber: Int, stopName: String, latLng: LatLng?) : Stop(stopName, stopNumber), Serializable {
    private val stopFeatures: ArrayList<StopFeature> = ArrayList()
    private val latLng: SerializableLatLng = SerializableLatLng(latLng)

    fun loadFeatures(document: JSONObject) {
        try {
            val features = document.getJSONArray(STOP_FEATURE_TAG)
            stopFeatures.clear()

            (0 until features.length()).mapTo(stopFeatures) { StopFeature(features.getJSONObject(it)) }
        } catch (e: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

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

    companion object {
        private val STOP_FEATURE_TAG = "stop-features"
    }
}
