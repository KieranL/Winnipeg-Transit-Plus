package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.common.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class SearchResults {
    private val stops: ArrayList<Stop> = ArrayList()

    fun loadStops(result: LoadResult<JSONObject>): SearchResults {
        if (result.result != null) {
            stops.clear()

            try {
                val stops = result.result.getJSONArray(TransitApiManager.STOP_TAG)

                if (stops.length() > 0)
                    for (s in 0 until stops.length()) {
                        val stopJson = stops.getJSONObject(s)
                        val stop = Stop(stopJson.getString(TransitApiManager.STOP_NAME_TAG), WinnipegTransitStopIdentifier(stopJson.getInt(TransitApiManager.STOP_NUMBER_TAG)))
                        this.stops.add(stop)
                        stop.latLng = getLatLng(stopJson)
                    }
            } catch (e: JSONException) {

            }
        }

        return this
    }

    fun getStops(): List<Stop> {
        return stops
    }

    private fun getLatLng(stop: JSONObject): GeoLocation? {
        return try {
            val geographic = stop.getJSONObject(TransitApiManager.STOP_CENTRE_TAG).getJSONObject(TransitApiManager.GEOGRAPHIC_TAG)
            GeoLocation(geographic.getDouble(WinnipegTransitService.LATITUDE_TAG), geographic.getDouble(WinnipegTransitService.LONGITUDE_TAG))
        } catch (e: JSONException) {
            null
        }

    }

    fun clear() {
        stops.clear()
    }

    operator fun get(position: Int): Stop {
        return stops[position]
    }
}
