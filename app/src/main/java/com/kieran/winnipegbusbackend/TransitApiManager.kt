package com.kieran.winnipegbusbackend

import android.location.Location
import android.os.AsyncTask
import com.kieran.winnipegbus.BuildConfig

import com.kieran.winnipegbusbackend.enums.SearchQueryType

import org.json.JSONObject
import java.net.HttpURLConnection

import java.net.URL
import java.util.*

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
    private val URL_FORMAT = "http://api.winnipegtransit.com/v3/%s.json?usage=short&api-key=" + BuildConfig.winnipeg_transit_api_key + "%s"
    var lastQueryTime: StopTime? = StopTime()
        private set

    fun getJson(path: String): LoadResult<JSONObject> {
        return try {
            val url = URL(path)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection

            if(con.responseCode == 503)
                return LoadResult(null, RateLimitedException())

            val stream = con.inputStream
            val s = Scanner(stream).useDelimiter("\\A")
            val myString = if (s.hasNext()) s.next() else ""
            stream.close()

            val obj = JSONObject(myString)
            lastQueryTime = StopTime.convertStringToStopTime(obj.getString(QUERY_TIME))

            LoadResult(obj, null)
        } catch (ex: Exception) {
            LoadResult(null, ex)
        }
    }

    fun getJsonAsync(path: String, listener: OnJsonLoadResultReceiveListener): AsyncTask<*, *, *> {
        return object : AsyncTask<String, Void, LoadResult<JSONObject>>() {
            override fun doInBackground(vararg strings: String): LoadResult<JSONObject> {
                return getJson(path)
            }

            override fun onPostExecute(result: LoadResult<JSONObject>) {
                listener.onReceive(result)
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path)
    }

    fun generateStopNumberURL(stopNumber: Int, routeNumbers: List<Int>?, startTime: StopTime?, endTime: StopTime?): String {
        val parameters = ArrayList<URLParameter>()

        if (routeNumbers != null)
            parameters.add(URLParameter(ROUTE_PARAMETER, routeNumbers))

        if (startTime != null) {
            startTime.decreaseMilliSeconds(START_TIME_DECREASE.toLong()) //decrease start time for API inconsistency? not sure what the reason this is for

            parameters.add(URLParameter(START_TIME_PARAMETER, startTime.toURLTimeString()))
            startTime.decreaseMilliSeconds((-START_TIME_DECREASE).toLong())
        }

        if (endTime != null)
            parameters.add(URLParameter(END_TIME_PARAMETER, endTime.toURLTimeString()))

        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + stopNumber + FORWARD_SLASH + SCHEDULE_PARAMETER, parameters)
    }

    fun generateStopNumberURL(stopNumber: Int, routeNumber: Int, startTime: StopTime, endTime: StopTime?): String {
        val routeFilter = ArrayList<Int>()
        routeFilter.add(routeNumber)

        return generateStopNumberURL(stopNumber, routeFilter, startTime, endTime)
    }

    fun generateSearchQuery(search: String): SearchQuery {
        return try {
            val routeNumber = Integer.parseInt(search)
            generateSearchQuery(routeNumber)
        } catch (e: Exception) {
            SearchQuery(search, createUrl(STOPS_PARAMETER + COLON + createURLFriendlyString(search), null), SearchQueryType.GENERAL)
        }

    }

    fun generateSearchQuery(routeNumber: Int): SearchQuery {
        val parameters = arrayOf(URLParameter(ROUTE_PARAMETER, Integer.toString(routeNumber))).toList()
        val url = createUrl(STOPS_PARAMETER, parameters)

        return SearchQuery(Integer.toString(routeNumber), url, SearchQueryType.ROUTE_NUMBER)
    }

    fun generateSearchQuery(key: RouteKey): SearchQuery {
        val parameters = arrayOf(URLParameter(VARIANT_PARAMETER, key.keyString)).toList()
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
        val parameters = arrayOf(URLParameter(DISTANCE_PARAMETER, Integer.toString(totalRadius)), URLParameter(LATITUDE_PARAMETER, java.lang.Double.toString(location.latitude)), URLParameter(LONGITUDE_PARAMETER, java.lang.Double.toString(location.longitude))).toList()
        val url = createUrl(STOPS_PARAMETER, parameters)
        return SearchQuery("NearbyStops", url, SearchQueryType.NEARBY)
    }

    fun generateLocationQueryUrl(query: String): String {
        return createUrl(LOCATIONS_PARAMETER + COLON + createURLFriendlyString(query), null)
    }

    fun generateFindStopUrl(stopNumber: Int): String {
        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber), null)
    }

    private fun createUrl(path: String, parameters: List<URLParameter>?): String {
        var parameterString = ""

        parameters?.forEach { parameterString += "&" + it.toString() }

        return String.format(URL_FORMAT, path, parameterString)
    }

    interface OnJsonLoadResultReceiveListener {
        fun onReceive(result: LoadResult<JSONObject>)
    }
}

class RateLimitedException : Exception()
