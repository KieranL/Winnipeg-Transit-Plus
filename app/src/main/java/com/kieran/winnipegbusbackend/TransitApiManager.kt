package com.kieran.winnipegbusbackend

import android.location.Location
import android.os.AsyncTask

import com.kieran.winnipegbusbackend.enums.SearchQueryType

import org.json.JSONObject

import java.net.URL
import java.util.ArrayList

object TransitApiManager {
    private val START_TIME_DECREASE = 10000
    private val QUERY_TIME = "query-time"
    private val ROUTE_PARAMETER = "route"
    private val END_TIME_PARAMETER = "end"
    private val START_TIME_PARAMETER = "start"
    private val STOP_FEATURE_PARAMETER = "features"
    private val VARIANT_PARAMETER = "variant"
    private val STOPS_PARAMETER = "stops"
    private val FORWARD_SLASH = "/"
    private val COLON = ":"
    private val SCHEDULE_PARAMETER = "schedule"
    private val DISTANCE_PARAMETER = "distance"
    private val LATITUDE_PARAMETER = "lat"
    private val LONGITUDE_PARAMETER = "lon"
    private val SERVICE_ADVISORIES_PARAMETER = "service-advisories"
    private val LOCATIONS_PARAMETER = "locations"
    private val URL_FORMAT = "http://api.winnipegtransit.com/v2/%s.json?usage=short&api-key=FTy2QN8ts293ZlhYP1t%s"
    var lastQueryTime: StopTime? = StopTime()

    fun getJson(path: String): LoadResult<JSONObject> {
        try {
            val url = URL(path)
            val s = java.util.Scanner(url.openStream()).useDelimiter("\\A")
            val myString = if (s.hasNext()) s.next() else ""

            val obj = JSONObject(myString)
            lastQueryTime = StopTime.convertStringToStopTime(obj.getString(QUERY_TIME))

            return LoadResult(obj, null)
        } catch (ex: Exception) {
            return LoadResult<JSONObject>(null, ex)
        }

    }

    fun getJsonAsync(path: String, listener: OnJsonLoadResultReceiveListener): AsyncTask<*, *, *> {
        return object : AsyncTask<String, Void, LoadResult<JSONObject>>() {
            override fun doInBackground(vararg strings: String): LoadResult<JSONObject> {
                return getJson(path)
            }

            override fun onPostExecute(result: LoadResult<JSONObject>) {
                listener.OnReceive(result)
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path)
    }

    fun generateStopNumberURL(stopNumber: Int, routeNumbers: List<Int>?, startTime: StopTime?, endTime: StopTime?): String {
        val parameters = arrayOfNulls<URLParameter>(3)

        if (routeNumbers != null)
            parameters[0] = URLParameter(ROUTE_PARAMETER, routeNumbers)

        if (startTime != null) {
            startTime.decreaseMilliSeconds(START_TIME_DECREASE.toLong()) //decrease start time for API inconsistency? not sure what the reason this is for

            parameters[1] = URLParameter(START_TIME_PARAMETER, startTime.toURLTimeString())
            startTime.decreaseMilliSeconds((-START_TIME_DECREASE).toLong())
        }

        if (endTime != null)
            parameters[2] = URLParameter(END_TIME_PARAMETER, endTime.toURLTimeString())

        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + stopNumber + FORWARD_SLASH + SCHEDULE_PARAMETER, parameters)
    }

    fun generateStopNumberURL(stopNumber: Int, routeNumber: Int, startTime: StopTime, endTime: StopTime): String {
        val routeFilter = ArrayList<Int>()
        routeFilter.add(routeNumber)

        return generateStopNumberURL(stopNumber, routeFilter, startTime, endTime)
    }

    fun generateSearchQuery(search: String): SearchQuery {
        try {
            val routeNumber = Integer.parseInt(search)
            return generateSearchQuery(routeNumber)
        } catch (e: Exception) {
            return SearchQuery(search, createUrl(STOPS_PARAMETER + COLON + createURLFriendlyString(search), null), SearchQueryType.GENERAL)
        }

    }

    fun generateSearchQuery(routeNumber: Int): SearchQuery {
        val parameters = arrayOf(URLParameter(ROUTE_PARAMETER, Integer.toString(routeNumber)))
        val url = createUrl(STOPS_PARAMETER, parameters)

        return SearchQuery(Integer.toString(routeNumber), url, SearchQueryType.ROUTE_NUMBER)
    }

    fun generateSearchQuery(key: RouteKey): SearchQuery {
        val parameters = arrayOf(URLParameter(VARIANT_PARAMETER, key.keyString))
        val url = createUrl(STOPS_PARAMETER, parameters)

        return SearchQuery(key.keyString, url, SearchQueryType.ROUTE_NUMBER)
    }

    fun generateStopFeaturesUrl(stopNumber: Int): String {
        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber) + FORWARD_SLASH + STOP_FEATURE_PARAMETER, null)
    }

    fun generateServiceAdvisoriesUrl(): String {
        return createUrl(SERVICE_ADVISORIES_PARAMETER, null)
    }

    private fun createURLFriendlyString(s: String): String {
        return s.replace("\\s+".toRegex(), "+")
    }

    fun generateSearchQuery(location: Location, radius: Int): SearchQuery {
        val totalRadius = Math.round(location.accuracy) + radius
        val parameters = arrayOf(URLParameter(DISTANCE_PARAMETER, Integer.toString(totalRadius)), URLParameter(LATITUDE_PARAMETER, java.lang.Double.toString(location.latitude)), URLParameter(LONGITUDE_PARAMETER, java.lang.Double.toString(location.longitude)))
        val url = createUrl(STOPS_PARAMETER, parameters)
        return SearchQuery("NearbyStops", url, SearchQueryType.NEARBY)
    }

    fun generateLocationQueryUrl(query: String): String {
        return createUrl(LOCATIONS_PARAMETER + COLON + createURLFriendlyString(query), null)
    }

    fun generateFindStopUrl(stopNumber: Int): String {
        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber), null)
    }

    private fun createUrl(path: String, parameters: Array<URLParameter>?): String {
        var parameterString = ""

        if (parameters != null) {
            for (p in parameters) {
                if (p != null)
                    parameterString += "&" + p.toString()
            }
        }

        return String.format(URL_FORMAT, path, parameterString)
    }

    interface OnJsonLoadResultReceiveListener {
        fun OnReceive(result: LoadResult<JSONObject>)
    }
}