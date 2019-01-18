package com.kieran.winnipegbusbackend.winnipegtransit

import com.kieran.winnipegbusbackend.StopFeatures
import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.interfaces.Location
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import com.kieran.winnipegbusbackend.interfaces.TransitService

object WinnipegTransitService : TransitService {
    override fun getStopSchedule(stop: StopIdentifier, routes: ArrayList<RouteIdentifier>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStopDetails(stop: StopIdentifier, stopFeatures: StopFeatures): StopFeatures {
        val result = TransitApiManager.getJson(TransitApiManager.generateStopFeaturesUrl(stopFeatures.number))

        if (result.exception != null) {
            throw result.exception
        }

        if (result.result != null)
            stopFeatures.loadFeatures(result.result)

        return stopFeatures
    }

    override fun getRouteStops(route: RouteIdentifier) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findStop(name: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findClosestStops(location: Location, distance: Int, stopCount: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLastQueryTime(): StopTime {
        return TransitApiManager.lastQueryTime
    }

    override fun getScheduleType(): ScheduleType {
        return ScheduleType.LIVE
    }
}