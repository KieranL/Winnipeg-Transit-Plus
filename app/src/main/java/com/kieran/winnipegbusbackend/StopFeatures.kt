package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList

class StopFeatures(stopNumber: Int, stopName: String, latLng: LatLng) : Stop(stopName, stopNumber), Serializable {
    private val stopFeatures: MutableList<StopFeature>
    private val latLng: SerializableLatLng

    init {
        this.latLng = SerializableLatLng(latLng)
        stopFeatures = ArrayList()
    }

    fun loadFeatures(document: JSONObject) {
        try {
            val features = document.getJSONArray(STOP_FEATURE_TAG)
            stopFeatures.clear()

            for (f in 0 until features.length())
                stopFeatures.add(StopFeature(features.getJSONObject(f)))
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
