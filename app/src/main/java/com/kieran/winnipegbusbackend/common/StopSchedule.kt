package com.kieran.winnipegbusbackend.common


import com.kieran.winnipegbusbackend.interfaces.ScheduledStopKey
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import java.util.*

class StopSchedule(stopName: String, stopIdentifier: StopIdentifier, private var routeList: ArrayList<RouteSchedule>, latLng: GeoLocation?) : Stop(stopName, stopIdentifier, latLng) {

    val scheduledStops: List<ScheduledStop>
        get() {
            val scheduledStops = ArrayList<ScheduledStop>()

            for (r in routeList)
                scheduledStops.addAll(r.scheduledStops)

            return scheduledStops
        }

    val scheduledStopsSorted: List<ScheduledStop>
        get() {
            val scheduledStops = scheduledStops

            Collections.sort(scheduledStops)

            return scheduledStops
        }

    fun getRouteList(): List<RouteSchedule> {
        return routeList
    }

    fun getScheduledStopByKey(key: ScheduledStopKey): ScheduledStop? {
        for (scheduledStop in scheduledStops)
            if (scheduledStop.key == key)
                return scheduledStop
        return null
    }

    fun createStopFeatures(): StopFeatures {
        return StopFeatures(identifier, name, latLng)
    }
}
