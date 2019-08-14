package com.kieran.winnipegbusbackend.agency.winnipegtransit

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.common.StopSchedule
import com.kieran.winnipegbusbackend.common.*
import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.enums.SearchQueryType
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.exceptions.RateLimitedException
import com.kieran.winnipegbusbackend.exceptions.TransitDataNotFoundException
import com.kieran.winnipegbusbackend.interfaces.*
import com.rollbar.android.Rollbar
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.util.*

object WinnipegTransitService : TransitService {
    override suspend fun getStopSchedule(stop: StopIdentifier, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>): StopSchedule {
        val stopNumber = (stop as WinnipegTransitStopIdentifier).stopNumber
        val url = TransitApiManager.generateStopNumberURL(stopNumber, routes.map { (it as WinnipegTransitRouteIdentifier).routeNumber }, null, endTime)
        val result = TransitApiManager.getJson(url)

        if (result.result != null)
            return createStopSchedule(result.result, WinnipegTransitStopIdentifier(stopNumber))
        else
            throw result.exception!!
    }

    override suspend fun getStopDetails(stop: StopIdentifier, stopFeatures: StopFeatures): StopFeatures {
        val result = TransitApiManager.getJson(TransitApiManager.generateStopFeaturesUrl((stop as WinnipegTransitStopIdentifier).stopNumber))

        if (result.exception != null) {
            throw result.exception
        }

        if (result.result != null) {
            try {
                val features = result.result.getJSONArray("stop-features")
                val stopFeatureList = ArrayList<StopFeature>()

                (0 until features.length()).mapNotNullTo(stopFeatureList) {
                    try {
                        val featureNode = features.getJSONObject(it)
                        val featureName = featureNode.getString("name")
                        val featureCount = featureNode.getInt("count")
                        StopFeature(featureCount, featureName)
                    } catch (ex: JSONException) {
                        Rollbar.instance()?.error(ex)
                        null
                    }
                }

                stopFeatures.loadFeatures(stopFeatureList)
            } catch (ex: JSONException) {
                Rollbar.instance()?.error(ex)
            }

        }

        return stopFeatures
    }

    override suspend fun getRouteStops(route: RouteIdentifier): List<Stop> {
        val url = TransitApiManager.generateSearchQuery((route as WinnipegTransitRouteIdentifier).routeNumber)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override suspend fun findStop(name: String): List<Stop> {
        val url = TransitApiManager.generateSearchQuery(name)
        val result = TransitApiManager.getJson(url)

        if (result.result != null) {
            val searchResults = SearchResults()
            searchResults.loadStops(result)
            return searchResults.getStops()
        } else
            throw result.exception!!
    }

    override suspend fun findClosestStops(location: Location, distance: Int, stopCount: Int): List<Stop> {
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
                        val stopSchedule = createStopSchedule(result.result)

                        val scheduledStop1 = stopSchedule.getScheduledStopByKey(wpgTransitScheduledStopKey)

                        if (scheduledStop1 != null) {
                            val upcomingStop = UpcomingStop(stopSchedule, scheduledStop1.estimatedDepartureTime, scheduledStop1.key)
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
                } catch (ex: Exception) {
                    Rollbar.instance()?.error(ex)
                }

            }
        }.toCollection(tasks)

        tasks.joinAll()
        return upcomingStops
    }

    override fun supportedFeatures(): List<SupportedFeature> {
        return arrayListOf(SupportedFeature.UPCOMING_STOPS, SupportedFeature.STOP_FEATURES)
    }

    override fun getSearchQueryType(searchText: String): SearchQueryType {
        return try {
            val number = Integer.parseInt(searchText)

            if (number >= 10000) {
                SearchQueryType.STOP
            } else {
                SearchQueryType.ROUTE_NUMBER
            }
        } catch (e: Exception) {
            SearchQueryType.GENERAL
        }
    }

    override fun parseStringToStopIdentifier(text: String): StopIdentifier {
        val number = Integer.parseInt(text)
        return WinnipegTransitStopIdentifier(number)
    }

    override fun parseStringToRouteIdentifier(text: String): RouteIdentifier {
        return WinnipegTransitRouteIdentifier(text.toInt())
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
                Rollbar.instance()?.error(ex)
                throw TransitDataNotFoundException()
            }
        } else {
            throw result.exception!!
        }
    }

    fun isDownTownSpirit(routeNumber: Int): Boolean {
        val DT_SPIRIT_MAX_RT_NUM = 10
        return routeNumber < DT_SPIRIT_MAX_RT_NUM
    }

    private fun createStopSchedule(json: JSONObject, stop: WinnipegTransitStopIdentifier? = null): StopSchedule {
        try {
            val stopIdentifier: WinnipegTransitStopIdentifier
            val jsonObject = json.getJSONObject(STOP_SCHEDULE_TAG)
            val name = loadStopName(jsonObject.getJSONObject(STOP_TAG))
            val routes = loadRoutes(jsonObject)
            val latLng = loadLatLng(jsonObject.getJSONObject(STOP_TAG).getJSONObject(TransitApiManager.STOP_CENTRE_TAG).getJSONObject(TransitApiManager.GEOGRAPHIC_TAG))

            stopIdentifier = stop ?: loadStopNumber(jsonObject.getJSONObject(STOP_TAG))!!
            return StopSchedule(name!!, stopIdentifier, routes, latLng!!)
        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
            throw TransitDataNotFoundException()
        }
    }


    private fun loadLatLng(jsonObject: JSONObject): GeoLocation? {
        return try {
            return GeoLocation(jsonObject.getDouble(LATITUDE_TAG), jsonObject.getDouble(LONGITUDE_TAG))
        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
            null
        }

    }

    private fun loadStopName(jsonObject: JSONObject): String? {
        return try {
            jsonObject.getString(TransitApiManager.STOP_NAME_TAG)
        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
            null
        }

    }

    private fun loadStopNumber(jsonObject: JSONObject): WinnipegTransitStopIdentifier? {
        return try {
            return WinnipegTransitStopIdentifier(jsonObject.getInt(TransitApiManager.STOP_NUMBER_TAG))
        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
            null
        }

    }

    private fun loadRoutes(jsonObject: JSONObject): ArrayList<RouteSchedule> {
        val routeList = ArrayList<RouteSchedule>()
        try {
            val routes = jsonObject.getJSONArray(ROUTES_TAG)

            for (r in 0 until routes.length()) {
                val routeJson = routes.getJSONObject(r)

                val routeDetailsJson = routeJson.getJSONObject(ROUTE_TAG)

                val coverageType = CoverageTypes.getEnum(routeDetailsJson.getString(ROUTE_COVERAGE_TAG))
                val routeName = routeDetailsJson.getString(ROUTE_NAME_TAG)
                val routeIdentifier = WinnipegTransitRouteIdentifier(routeDetailsJson.getInt(ROUTE_NUMBER_TAG))
                val routeSchedule = RouteSchedule(routeIdentifier, routeName, coverageType, loadScheduledStops(routeJson, routeIdentifier, coverageType))

                routeList.add(routeSchedule)
            }
        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
        }

        return routeList
    }

    private fun loadScheduledStops(jsonObject: JSONObject, routeIdentifier: WinnipegTransitRouteIdentifier, coverageType: CoverageTypes): ArrayList<ScheduledStop> {
        val scheduledStops = ArrayList<ScheduledStop>()
        try {
            val jsonScheduledStops = jsonObject.getJSONArray(SCHEDULED_STOPS_TAG)

            (0 until jsonScheduledStops.length())
                    .map { jsonScheduledStops.getJSONObject(it) }
                    .forEach {
                        try {
                            val scheduledStop = createScheduleStop(it, routeIdentifier, coverageType)
                            scheduledStops.add(scheduledStop)
                        } catch (ex: Exception) {
                            Rollbar.instance()?.error(ex)
                        }
                    }

        } catch (ex: JSONException) {
            Rollbar.instance()?.error(ex)
        }

        return scheduledStops
    }

    private fun createScheduleStop(stop: JSONObject?, routeIdentifier: WinnipegTransitRouteIdentifier, coverageType: CoverageTypes): ScheduledStop {
        if (stop != null) {
            try {
                val times = stop.getJSONObject(STOP_TIMES_TAG)
                val departure = times.getJSONObject(DEPARTURE_TAG)

                val estimatedDepartureTime = StopTime.convertStringToStopTime(departure.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
                val scheduledDepartureTime = StopTime.convertStringToStopTime(departure.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)

                var scheduledArrivalTime: StopTime? = null
                var estimatedArrivalTime: StopTime? = null
                var hasBikeRack = false
                var hasWifi = false
                var busIdentifier: WinnipegTransitVehicleIdentifier? = null
                var isTwoBus = false

                if(stop.has(BUS_INFO_TAG)) {
                    val bus = stop.getJSONObject(BUS_INFO_TAG)

                    hasBikeRack = bus.getBoolean(BIKE_RACK_TAG)
                    hasWifi = bus.getBoolean(WIFI_TAG)
                    val busNumber = bus.getInt(BUS_NUMBER_TAG)
                    busIdentifier = WinnipegTransitVehicleIdentifier(busNumber)
                    isTwoBus =  TWO_BUS_NUMBERS.contains(busNumber)
                }

                val key = WinnipegTransitScheduledStopKey(stop.getString(STOP_KEY_TAG))
                val isCancelled = stop.getBoolean(CANCELLED_STATUS_TAG)
                val variant = stop.getJSONObject(VARIANT_TAG)
                val routeVariantName = variant.getString(VARIANT_NAME_TAG)
                val routeKey = WinnipegTransitTripIdentifier(variant.getString(VARIANT_KEY_TAG))

                if (times.has(ARRIVAL_TAG)) {
                    val arrival = times.getJSONObject(ARRIVAL_TAG)
                    scheduledArrivalTime = StopTime.convertStringToStopTime(arrival.getString(SCHEDULED_TAG), TransitApiManager.API_DATE_FORMAT)
                    estimatedArrivalTime = StopTime.convertStringToStopTime(arrival.getString(ESTIMATED_TAG), TransitApiManager.API_DATE_FORMAT)
                }

                return ScheduledStop(routeVariantName, estimatedArrivalTime, estimatedDepartureTime!!, scheduledArrivalTime, scheduledDepartureTime!!, isCancelled, hasBikeRack, hasWifi, busIdentifier, key, routeKey, routeIdentifier, coverageType, isTwoBus)
            } catch (ex: JSONException) {
                Rollbar.instance()?.error(ex)
            }
        }

        throw TransitDataNotFoundException()
    }

    override fun getAgencyId(): Long {
        return 2
    }

    private val STOP_TAG = "stop"
    private val STOP_SCHEDULE_TAG = "stop-schedule"
    private val ROUTES_TAG = "route-schedules"
    val LATITUDE_TAG = "latitude"
    val LONGITUDE_TAG = "longitude"

    //ROUTE
    private val ROUTE_TAG = "route"
    private val ROUTE_COVERAGE_TAG = "coverage"
    private val ROUTE_NUMBER_TAG = "number"
    private val ROUTE_NAME_TAG = "name"
    private val SCHEDULED_STOPS_TAG = "scheduled-stops"


    //SCHEDULED_STOP
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

    val TWO_BUS_NUMBERS = arrayListOf(971, 972, 973, 974, 975, 976, 977, 978, 979, 981, 982, 983, 984, 985, 986, 987, 988, 989, 990, *(371..399).toList().toTypedArray())

}