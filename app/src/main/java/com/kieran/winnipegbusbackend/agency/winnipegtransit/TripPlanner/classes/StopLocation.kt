package com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes

import com.kieran.winnipegbusbackend.common.StopSchedule
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

    constructor(location: StopSchedule?) : super(location?.latLng!!, location.name)
}
