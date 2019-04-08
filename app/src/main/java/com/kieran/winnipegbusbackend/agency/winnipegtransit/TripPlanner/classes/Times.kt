package com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes

import com.kieran.winnipegbusbackend.common.StopTime
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TransitApiManager

import org.json.JSONException
import org.json.JSONObject

class Times(times: JSONObject) {
    var totalTime: Int = 0
    var walkingTime: Int = 0
    var waitingTime: Int = 0
    var ridingTime: Int = 0
    var startTime: StopTime? = null
    var endTime: StopTime? = null

    init {
        try {
            val durations = times.getJSONObject("durations")

            startTime = StopTime.convertStringToStopTime(times.getString("start"), TransitApiManager.API_DATE_FORMAT)
            endTime = StopTime.convertStringToStopTime(times.getString("end"), TransitApiManager.API_DATE_FORMAT)

            totalTime = durations.getInt("total")
            walkingTime = durations.getInt("walking")
            waitingTime = durations.getInt("waiting")
            ridingTime = durations.getInt("riding")
        } catch (e: JSONException) {

        }
    }
}
