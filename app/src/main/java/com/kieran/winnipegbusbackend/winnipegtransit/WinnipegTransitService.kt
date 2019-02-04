package com.kieran.winnipegbusbackend.winnipegtransit

import com.kieran.winnipegbusbackend.*
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.interfaces.Location
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import com.kieran.winnipegbusbackend.interfaces.TransitService
import com.kieran.winnipegbusbackend.shared.GeoLocation

object WinnipegTransitService : TransitService {
    override fun getStopSchedule(stop: StopIdentifier, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>): StopSchedule {
        val stopNumber = (stop as WinnipegTransitStopIdentifier).stopNumber
        val url = TransitApiManager.generateStopNumberURL(stopNumber, routes.map { (it as WinnipegTransitRouteIdentifier).routeNumber }, null, endTime)
        val result = TransitApiManager.getJson(url)


        if (result.result != null)
            return StopSchedule(result.result, stopNumber)
        else
            throw result.exception!!
    }

    override fun getStopDetails(stop: StopIdentifier, stopFeatures: StopFeatures): StopFeatures {
        val result = TransitApiManager.getJson(TransitApiManager.generateStopFeaturesUrl((stop as WinnipegTransitStopIdentifier).stopNumber))

        if (result.exception != null) {
            throw result.exception
        }

        if (result.result != null)
            stopFeatures.loadFeatures(result.result)

        return stopFeatures
    }

    override fun getRouteStops(route: RouteIdentifier): List<Stop> {
        val url = TransitApiManager.generateSearchQuery((route as WinnipegTransitRouteIdentifier).routeNumber)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override fun findStop(name: String): List<Stop> {
        val url = TransitApiManager.generateSearchQuery(name)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override fun findClosestStops(location: Location, distance: Int, stopCount: Int): List<Stop> {
        val url = TransitApiManager.generateSearchQuery((location as GeoLocation), distance)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override fun getLastQueryTime(): StopTime {
        return TransitApiManager.lastQueryTime
    }

    override fun getScheduleType(): ScheduleType {
        return ScheduleType.LIVE
    }
}