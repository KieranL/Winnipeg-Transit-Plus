package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.agency.winnipegtransit.TransitApiManager
import com.kieran.winnipegbusbackend.common.StopTime
import com.kieran.winnipegbusbackend.enums.CoverageTypes
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable
import java.util.*

class RouteSchedule : Route, Serializable {
    private var stops: ArrayList<ScheduledStop>? = null

    val scheduledStops: List<ScheduledStop>?
        get() = stops

    constructor(jsonObject: JSONObject) {
        var jsonObject = jsonObject
        try {
            jsonObject = jsonObject.getJSONObject(ROUTE_TAG)

            coverageType = CoverageTypes.getEnum(jsonObject.getString(ROUTE_COVERAGE_TAG))
            routeName = jsonObject.getString(ROUTE_NAME_TAG)
            routeNumber = jsonObject.getInt(ROUTE_NUMBER_TAG)
        } catch (e: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

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
                            val scheduledStop = createScheduleStop(it)
                            stops?.add(scheduledStop)
//                            stops?.add(ScheduledStop(it, this))
                        } catch (e: Exception) {
                            //blank
                        }
                    }

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun createScheduleStop(stop: JSONObject?): ScheduledStop {
        if (stop != null) {
            val times = stop.getJSONObject(ScheduledStop.STOP_TIMES_TAG)
            val departure = times.getJSONObject(ScheduledStop.DEPARTURE_TAG)

            val estimatedDepartureTime = StopTime.convertStringToStopTime(departure.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
            val scheduledDepartureTime = StopTime.convertStringToStopTime(departure.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)


            if (times.has(ARRIVAL_TAG)) {
                val arrival = times.getJSONObject(ARRIVAL_TAG)
                scheduledArrivalTime = StopTime.convertStringToStopTime(arrival.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)
                estimatedArrivalTime = StopTime.convertStringToStopTime(arrival.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
            }
            return ScheduledStop(routeName,
        }
    }

    companion object {
        private val ROUTE_TAG = "route"
        private val ROUTE_COVERAGE_TAG = "coverage"
        private val ROUTE_NUMBER_TAG = "number"
        private val ROUTE_NAME_TAG = "name"
        private val SCHEDULED_STOPS_TAG = "scheduled-stops"
    }
}
