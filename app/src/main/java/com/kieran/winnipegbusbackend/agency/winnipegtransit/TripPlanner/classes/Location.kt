package com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.common.SerializableLatLng

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.Locale

open class Location : Serializable {
    private lateinit var point: SerializableLatLng
    var title: String
        internal set

    open val urlString: String
        get() = String.format(Locale.CANADA, "geo/%f,%f", point.latitude, point.longitude)

    constructor(location: JSONObject) {
        getLatLngFromLocation(location)
        title = "Location"
    }

    constructor(location: JSONObject, title: String) {
        getLatLngFromLocation(location)
        this.title = title
    }

    constructor(location: android.location.Location, title: String) {
        this.title = title
        point = SerializableLatLng(LatLng(location.latitude, location.longitude))
    }

    constructor(location: LatLng?, title: String?) {
        this.title = title ?: ""
        point = SerializableLatLng(LatLng(location?.latitude
                ?: 0.0, location?.longitude ?: 0.0))
    }

    private fun getLatLngFromLocation(location: JSONObject) {
        try {
            val centre = location.getJSONObject("centre")
            val geographic = centre.getJSONObject("geographic")
            point = SerializableLatLng(LatLng(geographic.getDouble("latitude"), geographic.getDouble("longitude")))
        } catch (e: JSONException) {

        }
    }

    override fun equals(other: Any?): Boolean {
        val otherLocation = other as Location?
        return otherLocation != null && otherLocation.point.latitude == point.latitude && otherLocation.point.longitude == point.longitude
    }
}
