package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.ScheduledStopKey
import com.kieran.winnipegbusbackend.interfaces.TripIdentifier
import com.kieran.winnipegbusbackend.interfaces.VehicleIdentifier

import java.io.Serializable

class ScheduledStop(
        val routeVariantName: String,
        val estimatedArrivalTime: StopTime?,
        val estimatedDepartureTime: StopTime,
        val scheduledArrivalTime: StopTime?,
        val scheduledDepartureTime: StopTime,
        val isCancelled: Boolean,
        val hasBikeRack: Boolean,
        val hasWifi: Boolean,
        val vehicleIdentifier: VehicleIdentifier?,
        val key: ScheduledStopKey,
        val routeKey: TripIdentifier,
        val routeIdentifier: RouteIdentifier,
        val coverageType: CoverageTypes,
        var isTwoBus: Boolean
) : Serializable, Comparable<ScheduledStop> {

    val timeStatus: String
        get() = if (isCancelled) "Cancelled" else StopTime.getTimeStatus(estimatedDepartureTime, scheduledDepartureTime)

    override fun compareTo(other: ScheduledStop): Int {
        val time = if (isCancelled) scheduledDepartureTime else estimatedDepartureTime

        return time.compareTo(other.estimatedDepartureTime)
    }
}