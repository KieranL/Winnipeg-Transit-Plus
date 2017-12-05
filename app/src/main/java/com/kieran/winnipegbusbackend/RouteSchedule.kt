package com.kieran.winnipegbusbackend

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable
import java.util.ArrayList

class RouteSchedule : Route, Serializable {

    private val stops: MutableList<ScheduledStop>

    val scheduledStops: List<ScheduledStop>
        get() = stops

    constructor(routeSchedule: RouteSchedule) : super(routeSchedule) {}

    constructor(jsonObject: JSONObject) : super(jsonObject) {

        stops = ArrayList()

        loadScheduledStops(jsonObject)
    }

    private fun loadScheduledStops(jsonObject: JSONObject) {
        try {
            val scheduledStops = jsonObject.getJSONArray(SCHEDULED_STOPS_TAG)

            for (s in 0 until scheduledStops.length()) {
                val stop = scheduledStops.getJSONObject(s)
                try {
                    stops.add(ScheduledStop(stop, this))
                } catch (e: Exception) {
                    //blank
                }

            }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    companion object {
        private val SCHEDULED_STOPS_TAG = "scheduled-stops"
    }
}
