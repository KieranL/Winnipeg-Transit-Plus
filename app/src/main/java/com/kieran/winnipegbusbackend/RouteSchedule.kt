package com.kieran.winnipegbusbackend

import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class RouteSchedule : Route, Serializable {
    fun compareTo(other: Nothing): Int {
        return super.compareTo(other)
    }

    private var stops: ArrayList<ScheduledStop>? = null

    val scheduledStops: List<ScheduledStop>?
        get() = stops

    constructor(routeSchedule: RouteSchedule) : super(routeSchedule) {}

    constructor(jsonObject: JSONObject) : super(jsonObject) {

        stops = ArrayList()

        loadScheduledStops(jsonObject)
    }

    private fun loadScheduledStops(jsonObject: JSONObject) {
        try {
            val scheduledStops = jsonObject.getJSONArray(SCHEDULED_STOPS_TAG)

            (0 until scheduledStops.length())
                    .map { scheduledStops.getJSONObject(it) }
                    .forEach {
                        try {
                            stops?.add(ScheduledStop(it, this))
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