package com.kieran.winnipegbusbackend.winnipegtransit

import com.kieran.winnipegbusbackend.*
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.exceptions.RateLimitedException
import com.kieran.winnipegbusbackend.exceptions.TransitDataNotFoundException
import com.kieran.winnipegbusbackend.interfaces.*
import com.kieran.winnipegbusbackend.shared.GeoLocation
import kotlinx.coroutines.*
import org.json.JSONException
import java.io.FileNotFoundException
import java.util.ArrayList

object WinnipegTransitService : TransitService {
    override suspend fun getStopSchedule(stop: StopIdentifier, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>): StopSchedule {
        val stopNumber = (stop as WinnipegTransitStopIdentifier).stopNumber
        val url = TransitApiManager.generateStopNumberURL(stopNumber, routes.map { (it as WinnipegTransitRouteIdentifier).routeNumber }, null, endTime)
        val result = TransitApiManager.getJson(url)


        if (result.result != null)
            return StopSchedule(result.result, stopNumber)
        else
            throw result.exception!!
    }

    override suspend fun getStopDetails(stop: StopIdentifier, stopFeatures: StopFeatures): StopFeatures {
        val result = TransitApiManager.getJson(TransitApiManager.generateStopFeaturesUrl((stop as WinnipegTransitStopIdentifier).stopNumber))

        if (result.exception != null) {
            throw result.exception
        }

        if (result.result != null)
            stopFeatures.loadFeatures(result.result)

        return stopFeatures
    }

    override suspend fun getRouteStops(route: RouteIdentifier): List<FavouriteStop> {
        val url = TransitApiManager.generateSearchQuery((route as WinnipegTransitRouteIdentifier).routeNumber)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override suspend fun findStop(name: String): List<FavouriteStop> {
        val url = TransitApiManager.generateSearchQuery(name)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override suspend fun findClosestStops(location: Location, distance: Int, stopCount: Int): List<FavouriteStop> {
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

    override suspend fun getUpcomingStops(key: TripIdentifier, scheduledStopKey: ScheduledStopKey, after: StopTime): List<UpcomingStop> {
        val upcomingStops = ArrayList<UpcomingStop>()
        val variant = key as WinnipegTransitTripIdentifier
        val wpgTransitScheduledStopKey = scheduledStopKey as WinnipegTransitScheduledStopKey
        val stopNumbers = getUpcomingStopNumbers(variant, wpgTransitScheduledStopKey.stopNumber)
        val tasks = ArrayList<Job>()

        stopNumbers.map {
            GlobalScope.launch(Dispatchers.IO) {
                val latest = if (after.milliseconds > getLastQueryTime().milliseconds) after else TransitApiManager.lastQueryTime

                try {
                    val result = TransitApiManager.getJson(TransitApiManager.generateStopNumberURL(it, variant.routeNumber, latest, null))

                    if (result.result != null) {
                        val stopSchedule = StopSchedule(result.result)
                        val scheduledStop1 = stopSchedule.getScheduledStopByKey(wpgTransitScheduledStopKey)

                        if (scheduledStop1 != null) {
                            val upcomingStop = UpcomingStop(stopSchedule, scheduledStop1.estimatedDepartureTime!!, scheduledStop1.key!!)
                            upcomingStops.add(upcomingStop)
                        }
                    } else if (result.exception != null) {
                        if (result.exception is FileNotFoundException || result.exception is RateLimitedException) {
                            tasks.forEach { task ->
                                task.cancel()
                            }

                        }

                        throw result.exception
                    }
                } catch (e: Exception) {
                }

            }
        }.toCollection(tasks)

        tasks.joinAll()
        return upcomingStops
    }

    override fun supportedFeatures(): List<SupportedFeature> {
        return arrayListOf(SupportedFeature.UPCOMING_STOPS)
    }

    private fun getUpcomingStopNumbers(key: WinnipegTransitTripIdentifier, stopOnRoute: Int): List<Int> {
        val queryUrl = TransitApiManager.generateSearchQuery(key)
        val result = TransitApiManager.getJson(queryUrl)

        if (result.result != null) {
            try {
                val stops = result.result.getJSONArray("stops")
                val stopNumbers = ArrayList<Int>()

                for (i in 0 until stops.length()) {
                    stopNumbers.add(stops.getJSONObject(i).getInt("number"))
                }

                return stopNumbers
            } catch (ex: JSONException) {
                throw TransitDataNotFoundException()
            }
        }
        throw TransitDataNotFoundException()
    }
}