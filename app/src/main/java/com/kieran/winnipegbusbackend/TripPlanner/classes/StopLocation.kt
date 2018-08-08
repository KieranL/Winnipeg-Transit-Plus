package com.kieran.winnipegbusbackend.TripPlanner.classes

import com.kieran.winnipegbusbackend.StopSchedule
import org.json.JSONException
import org.json.JSONObject

class StopLocation : Location {
    constructor(location: JSONObject) : super(location) {
        try {
            stopNumber = location.getInt("key")
            title = location.getString("name")
        } catch (e: JSONException) {

        }
    }

    internal var stopNumber: Int = 0

    constructor(location: StopSchedule?) : super(location?.getLatLng(), location?.name)
}
