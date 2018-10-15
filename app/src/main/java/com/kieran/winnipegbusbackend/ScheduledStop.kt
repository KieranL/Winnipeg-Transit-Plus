package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.enums.CoverageTypes

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class ScheduledStop(stop: JSONObject, private val parentRoute: RouteSchedule) : Serializable, Comparable<ScheduledStop> {
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
    var isCancelled: Boolean = false
        private set
    var hasBikeRack: Boolean = false
        private set
    var hasWifi: Boolean = false
        private set
    var busNumber: Int = 0
        private set
    var key: ScheduledStopKey? = null
        private set
    var routeKey: RouteKey? = null
        private set
    val isTwoBus: Boolean
        get() = TWO_BUS_NUMBERS.contains(busNumber)

    val timeStatus: String
        get() = if (isCancelled) "Cancelled" else StopTime.getTimeStatus(estimatedDepartureTime!!, scheduledDepartureTime!!)

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
        loadCancelledStatus(stop)
    }

    private fun loadKey(stop: JSONObject) {
        try {
            key = ScheduledStopKey(stop.getString(STOP_KEY_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    private fun loadCancelledStatus(stop: JSONObject) {
        try {
            isCancelled = stop.getBoolean(CANCELLED_STATUS_TAG)
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private fun loadBusInfo(stop: JSONObject) {
        try {
            val bus = stop.getJSONObject(BUS_INFO_TAG)

            hasBikeRack = bus.getBoolean(BIKE_RACK_TAG)
            hasWifi = bus.getBoolean(WIFI_TAG)
            busNumber = bus.getInt(BUS_NUMBER_TAG)
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

    fun hasArrivalTime(): Boolean {
        return estimatedArrivalTime != null && scheduledArrivalTime != null
    }

    override fun compareTo(other: ScheduledStop): Int {
        val time = if(isCancelled) scheduledDepartureTime else estimatedDepartureTime

        return time!!.compareTo(other.estimatedDepartureTime!!)
    }

    companion object {
        private val ARRIVAL_TAG = "arrival"
        private val DEPARTURE_TAG = "departure"
        private val STOP_TIMES_TAG = "times"
        private val ESTIMATED_TAG = "estimated"
        private val SCHEDULED_TAG = "scheduled"
        private val BUS_INFO_TAG = "bus"
        private val BIKE_RACK_TAG = "bike-rack"
        private val STOP_KEY_TAG = "key"
        private val VARIANT_NAME_TAG = "name"
        private val VARIANT_TAG = "variant"
        private val VARIANT_KEY_TAG = "key"
        private val WIFI_TAG = "wifi"
        private val BUS_NUMBER_TAG = "key"
        private val CANCELLED_STATUS_TAG = "cancelled"
        private val TWO_BUS_NUMBERS = intArrayOf(971, 972, 973, 974, 975, 976, 977, 978,979,981,982,983,984,985,986,987,989,990)
    }
}