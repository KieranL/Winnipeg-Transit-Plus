package com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner.classes

import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner.TimeMode

import java.io.Serializable
import java.util.Locale

class TripParameters : Serializable {
    var origin: Location? = null
    var destination: Location? = null
    var time: StopTime? = null
    var timeMode: TimeMode? = null

    val url: String
        get() = String.format(Locale.CANADA, "https://api.winnipegtransit.com/v3/trip-planner.json?origin=%s&destination=%s&mode=%s&time=%s&date=%s&api-key=FTy2QN8ts293ZlhYP1t", origin!!.urlString, destination!!.urlString, timeMode!!.urlParameter, time!!.toURLTimeString().split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1], time!!.toURLTimeString().split("T".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0])

    val isValid: Boolean
        get() = (origin != null
                && destination != null
                && timeMode != null
                && time != null
                && origin != destination)

    init {
        time = StopTime()
        timeMode = TimeMode.DEPART_AFTER
    }

    fun swapLocations() {
        val temp = origin
        origin = destination
        destination = temp
    }
}
