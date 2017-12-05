package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.enums.CoverageTypes

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class ScheduledStop(stop: JSONObject, private val parentRoute: RouteSchedule) : Serializable {

    var routeVariantName: String? = null
        private set
    var estimatedArrivalTime: StopTime? = null
        private set
    var estimatedDepartureTime: StopTime? = null
        private set
    var scheduledArrivalTime: StopTime? = null
        private set
    var scheduledDepartureTime: StopTime? = null
        private set
    private var hasBikeRack: Boolean = false
    private var hasEasyAccess: Boolean = false
    var key: ScheduledStopKey? = null
        private set
    var routeKey: RouteKey? = null
        private set

    val timeStatus: String
        get() = StopTime.getTimeStatus(estimatedDepartureTime!!, scheduledDepartureTime!!)

    val routeNumber: Int
        get() = parentRoute.routeNumber

    val coverageType: CoverageTypes
        get() = parentRoute.coverageType

    init {
        this.routeVariantName = parentRoute.routeName

        loadTimes(stop)
        loadVariantInfo(stop)
        loadBusInfo(stop)
        loadKey(stop)
    }

    private fun loadKey(stop: JSONObject) {
        try {
            key = ScheduledStopKey(stop.getString(STOP_KEY_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    private fun loadBusInfo(stop: JSONObject) {
        try {
            val bus = stop.getJSONObject(BUS_INFO_TAG)

            hasEasyAccess = bus.getBoolean(EASY_ACCESS_TAG)
            hasBikeRack = bus.getBoolean(BIKE_RACK_TAG)
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    private fun loadVariantInfo(stop: JSONObject) {
        try {
            val variant = stop.getJSONObject(VARIANT_TAG)
            routeVariantName = variant.getString(VARIANT_NAME_TAG)
            routeKey = RouteKey(variant.getString(VARIANT_KEY_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    private fun loadTimes(stop: JSONObject) {
        try {
            val times = stop.getJSONObject(STOP_TIMES_TAG)

            val departure = times.getJSONObject(DEPARTURE_TAG)

            estimatedDepartureTime = StopTime.convertStringToStopTime(departure.getString(ESTIMATED_TAG))
            scheduledDepartureTime = StopTime.convertStringToStopTime(departure.getString(SCHEDULED_TAG))


            if (times.has(ARRIVAL_TAG)) {
                val arrival = times.getJSONObject(ARRIVAL_TAG)
                scheduledArrivalTime = StopTime.convertStringToStopTime(arrival.getString(SCHEDULED_TAG))
                estimatedArrivalTime = StopTime.convertStringToStopTime(arrival.getString(ESTIMATED_TAG))
            }
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    fun hasBikeRack(): Boolean {
        return hasBikeRack
    }

    fun hasEasyAccess(): Boolean {
        return hasEasyAccess
    }

    fun hasArrivalTime(): Boolean {
        return estimatedArrivalTime != null && scheduledArrivalTime != null
    }

    companion object {
        private val ARRIVAL_TAG = "arrival"
        private val DEPARTURE_TAG = "departure"
        private val STOP_TIMES_TAG = "times"
        private val ESTIMATED_TAG = "estimated"
        private val SCHEDULED_TAG = "scheduled"
        private val BUS_INFO_TAG = "bus"
        private val BIKE_RACK_TAG = "bike-rack"
        private val EASY_ACCESS_TAG = "easy-access"
        private val STOP_KEY_TAG = "key"
        private val VARIANT_NAME_TAG = "name"
        private val VARIANT_TAG = "variant"
        private val VARIANT_KEY_TAG = "key"
    }
}