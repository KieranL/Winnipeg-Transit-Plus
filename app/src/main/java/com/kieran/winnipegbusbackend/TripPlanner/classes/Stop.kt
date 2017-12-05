package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONException
import org.json.JSONObject

class Stop(location: JSONObject) : Location(location) {
    internal var stopNumber: Int = 0

    init {

        try {
            stopNumber = location.getInt("key")
            title = location.getString("name")
        } catch (e: JSONException) {

        }

    }
}
