package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.common.StopTime
import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TransitApiManager
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitScheduledStopKey
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitTripIdentifier
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.ScheduledStopKey
import com.kieran.winnipegbusbackend.interfaces.TripIdentifier
import com.kieran.winnipegbusbackend.interfaces.VehicleIdentifier

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class ScheduledStop : Serializable, Comparable<ScheduledStop> {
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
    var vehicleIdentifier: VehicleIdentifier
        private set
    lateinit var key: ScheduledStopKey
        private set
    lateinit var routeKey: TripIdentifier
        private set
    val isTwoBus: Boolean
        get() = TWO_BUS_NUMBERS.contains(busNumber)

    val timeStatus: String
        get() = if (isCancelled) "Cancelled" else StopTime.getTimeStatus(estimatedDepartureTime!!, scheduledDepartureTime!!)

    var routeIdentifier: RouteIdentifier
        private set

    var coverageType: CoverageTypes
        private set

    constructor(stop: JSONObject, parentRoute: RouteSchedule) {
        this.routeVariantName = parentRoute.routeName
        routeIdentifier = WinnipegTransitRouteIdentifier(parentRoute.routeNumber)
        coverageType = parentRoute.coverageType

        loadTimes(stop)
        loadVariantInfo(stop)
        loadBusInfo(stop)
        loadKey(stop)
        loadCancelledStatus(stop)
    }

    constructor(routeVariantName: String, estimatedArrivalTime: StopTime?, estimatedDepartureTime: StopTime, scheduledArrivalTime: StopTime?, scheduledDepartureTime: StopTime, isCancelled: Boolean, hasBikeRack: Boolean, hasWifi: Boolean, vehicleIdentifier: VehicleIdentifier, key: ScheduledStopKey, routeKey: TripIdentifier, routeIdentifier: RouteIdentifier, coverageType: CoverageTypes) {
        this.routeVariantName = routeVariantName
        this.estimatedArrivalTime = estimatedArrivalTime
        this.estimatedDepartureTime = estimatedDepartureTime
        this.scheduledArrivalTime = scheduledArrivalTime
        this.scheduledDepartureTime = scheduledDepartureTime
        this.isCancelled = isCancelled
        this.hasBikeRack = hasBikeRack
        this.hasWifi = hasWifi
        this.vehicleIdentifier = vehicleIdentifier
        this.key = key
        this.routeKey = routeKey
        this.routeIdentifier = routeIdentifier
        this.coverageType = coverageType
    }

    private fun loadKey(stop: JSONObject) {
        try {
            key = WinnipegTransitScheduledStopKey(stop.getString(STOP_KEY_TAG))
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
            routeKey = WinnipegTransitTripIdentifier(variant.getString(VARIANT_KEY_TAG))
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    private fun loadTimes(stop: JSONObject) {
        try {
            val times = stop.getJSONObject(STOP_TIMES_TAG)

            val departure = times.getJSONObject(DEPARTURE_TAG)

            estimatedDepartureTime = StopTime.convertStringToStopTime(departure.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
            scheduledDepartureTime = StopTime.convertStringToStopTime(departure.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)


            if (times.has(ARRIVAL_TAG)) {
                val arrival = times.getJSONObject(ARRIVAL_TAG)
                scheduledArrivalTime = StopTime.convertStringToStopTime(arrival.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)
                estimatedArrivalTime = StopTime.convertStringToStopTime(arrival.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
            }
        } catch (ex: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

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
        private val TWO_BUS_NUMBERS = intArrayOf(971, 972, 973, 974, 975, 976, 977, 978,979,981,982,983,984,985,986,987,988,989,990)
    }
}