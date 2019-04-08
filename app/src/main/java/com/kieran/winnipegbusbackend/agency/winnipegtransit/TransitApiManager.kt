package com.kieran.winnipegbusbackend.agency.winnipegtransit

import android.os.AsyncTask
import com.kieran.winnipegbus.BuildConfig

import com.kieran.winnipegbusbackend.exceptions.RateLimitedException
import com.kieran.winnipegbusbackend.exceptions.TransitDataNotFoundException
import com.kieran.winnipegbusbackend.common.GeoLocation
import com.kieran.winnipegbusbackend.common.LoadResult
import com.kieran.winnipegbusbackend.common.StopTime

import org.json.JSONObject
import java.lang.Double

import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

object TransitApiManager {
    internal const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
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
    private val URL_FORMAT = "https://api.winnipegtransit.com/v3/%s.json?usage=short&api-key=" + BuildConfig.winnipeg_transit_api_key + "%s"

    //Stop model json tags
    val STOP_NAME_TAG = "name"
    val STOP_NUMBER_TAG = "number"
    val STOP_TAG = "stops"
    val GEOGRAPHIC_TAG = "geographic"
    val STOP_CENTRE_TAG = "centre"
    //End Stop model json tags

    var lastQueryTime: StopTime = StopTime()
        private set

    fun getJson(path: String): LoadResult<JSONObject> {
        return try {
            val url = URL(path)
            val con: HttpsURLConnection = url.openConnection() as HttpsURLConnection

            if(con.responseCode == 503)
                return LoadResult(null, RateLimitedException())
            else if(con.responseCode == 404)
                return LoadResult(null, TransitDataNotFoundException())

            val stream = con.inputStream
            val s = Scanner(stream).useDelimiter("\\A")
            val myString = if (s.hasNext()) s.next() else ""
            stream.close()

            val obj = JSONObject(myString)
            val newQueryTime = StopTime.convertStringToStopTime(obj.getString(QUERY_TIME), API_DATE_FORMAT)

            if(newQueryTime != null)
                lastQueryTime = newQueryTime

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

    fun generateSearchQuery(search: String): String {
        return try {
            val routeNumber = Integer.parseInt(search)
            generateSearchQuery(routeNumber)
        } catch (e: Exception) {
            createUrl(STOPS_PARAMETER + COLON + createURLFriendlyString(search), null)
        }
    }

    fun generateSearchQuery(routeNumber: Int): String {
        val parameters = arrayOf(URLParameter(ROUTE_PARAMETER, Integer.toString(routeNumber))).toList()
        return createUrl(STOPS_PARAMETER, parameters)
    }

    fun generateSearchQuery(key: WinnipegTransitTripIdentifier): String {
        val parameters = arrayOf(URLParameter(VARIANT_PARAMETER, key.keyString)).toList()
        return createUrl(STOPS_PARAMETER, parameters)
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

    fun generateSearchQuery(location: GeoLocation, radius: Int): String {
        val parameters = arrayOf(URLParameter(DISTANCE_PARAMETER, Integer.toString(radius)), URLParameter(LATITUDE_PARAMETER, Double.toString(location.latitude)), URLParameter(LONGITUDE_PARAMETER, Double.toString(location.longitude))).toList()
        return createUrl(STOPS_PARAMETER, parameters)
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
