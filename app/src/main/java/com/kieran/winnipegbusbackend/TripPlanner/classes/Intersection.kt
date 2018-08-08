package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONException
import org.json.JSONObject

import java.util.Locale

class Intersection(location: JSONObject) : Location(location) {
    internal lateinit var key: String
    internal lateinit var streetName: String
    internal lateinit var crossStreetName: String

    override val urlString: String
        get() = String.format(Locale.CANADA, "intersections/%s", key)

    init {
        try {
            val street = location.getJSONObject("street")
            val crossStreet = location.getJSONObject("cross-street")

            key = location.getString("key")
            streetName = street.getString("name")
            crossStreetName = crossStreet.getString("name")
            title = String.format(Locale.CANADA, "%s at %s", streetName, crossStreetName)
        } catch (e: JSONException) {

        }

    }
}
