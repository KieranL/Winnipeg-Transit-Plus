package com.kieran.winnipegbusbackend


import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.common.StopFeatures
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TransitApiManager
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitScheduledStopKey
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Collections

class StopSchedule : Stop {

    private val routeList = ArrayList<RouteSchedule>()
    private var latLng: LatLng? = null

    val scheduledStops: List<ScheduledStop>
        get() {
            val scheduledStops = ArrayList<ScheduledStop>()

            for (r in routeList)
                scheduledStops.addAll(r.scheduledStops!!)

            return scheduledStops
        }

    val scheduledStopsSorted: List<ScheduledStop>
        get() {
            val scheduledStops = scheduledStops

            Collections.sort(scheduledStops)

            return scheduledStops
        }

    constructor(jsonObject: JSONObject) {
        var jsonObject = jsonObject
        try {
            jsonObject = jsonObject.getJSONObject(STOP_SCHEDULE_TAG)
            loadStopNumber(jsonObject.getJSONObject(STOP_TAG))
            loadStopName(jsonObject.getJSONObject(STOP_TAG))
            loadRoutes(jsonObject)
            loadLatLng(jsonObject.getJSONObject(STOP_TAG).getJSONObject(TransitApiManager.STOP_CENTRE_TAG).getJSONObject(TransitApiManager.GEOGRAPHIC_TAG))
        } catch (e: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    constructor(jsonObject: JSONObject, stopIdentifier: StopIdentifier) : super(stopIdentifier) {
        var jsonObject = jsonObject
        try {
            jsonObject = jsonObject.getJSONObject(STOP_SCHEDULE_TAG)
            loadStopName(jsonObject.getJSONObject(STOP_TAG))
            loadRoutes(jsonObject)
            loadLatLng(jsonObject.getJSONObject(STOP_TAG).getJSONObject(TransitApiManager.STOP_CENTRE_TAG).getJSONObject(TransitApiManager.GEOGRAPHIC_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    @Throws(JSONException::class)
    private fun loadLatLng(jsonObject: JSONObject) {
        try {
            latLng = LatLng(jsonObject.getDouble(LATITUDE_TAG), jsonObject.getDouble(LONGITUDE_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    @Throws(JSONException::class)
    private fun loadRoutes(jsonObject: JSONObject) {
        try {
            val routes = jsonObject.getJSONArray(ROUTES_TAG)

            for (r in 0 until routes.length())
                routeList.add(RouteSchedule(routes.getJSONObject(r)))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    @Throws(JSONException::class)
    private fun loadStopName(jsonObject: JSONObject) {
        try {
            name = jsonObject.getString(TransitApiManager.STOP_NAME_TAG)
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    @Throws(JSONException::class)
    private fun loadStopNumber(jsonObject: JSONObject) {
        try {
            identifier = WinnipegTransitStopIdentifier(jsonObject.getInt(TransitApiManager.STOP_NUMBER_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    fun getRouteList(): List<RouteSchedule> {
        return routeList
    }

    fun getScheduledStopByKey(key: WinnipegTransitScheduledStopKey): ScheduledStop? {
        for (scheduledStop in scheduledStops)
            if (scheduledStop.key!!.equals(key))
                return scheduledStop
        return null
    }

    fun createStopFeatures(): StopFeatures {
        return StopFeatures(identifier, name, latLng)
    }

    fun getLatLng(): LatLng? {
        return latLng
    }

    companion object {
        private val STOP_TAG = "stop"
        private val STOP_SCHEDULE_TAG = "stop-schedule"
        private val ROUTES_TAG = "route-schedules"
        val LATITUDE_TAG = "latitude"
        val LONGITUDE_TAG = "longitude"
    }
}
