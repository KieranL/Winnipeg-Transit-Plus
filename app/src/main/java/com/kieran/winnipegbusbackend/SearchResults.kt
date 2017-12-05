package com.kieran.winnipegbusbackend

import com.google.android.gms.maps.model.LatLng

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class SearchResults {
    private val stops: MutableList<FavouriteStop>

    val length: Int
        get() = stops.size

    init {
        stops = ArrayList()
    }

    fun loadStops(result: LoadResult<JSONObject>): SearchResults {
        if (result.result != null) {
            stops.clear()

            try {
                val stops = result.result!!.getJSONArray(Stop.STOP_TAG)

                if (stops.length() > 0)
                    for (s in 0 until stops.length()) {
                        val stop = stops.getJSONObject(s)
                        val favouriteStop = FavouriteStop(stop.getString(Stop.STOP_NAME_TAG), stop.getInt(Stop.STOP_NUMBER_TAG))
                        this.stops.add(favouriteStop)

                        favouriteStop.latLng = getLatLng(stop)
                    }
            } catch (e: JSONException) {

            }

        }

        return this
    }

    fun getStops(): List<FavouriteStop> {
        return stops
    }

    private fun getLatLng(stop: JSONObject): LatLng? {
        try {
            val geographic = stop.getJSONObject(Stop.STOP_CENTRE_TAG).getJSONObject(Stop.GEOGRAPHIC_TAG)
            return LatLng(geographic.getDouble(StopSchedule.LATITUDE_TAG), geographic.getDouble(StopSchedule.LONGITUDE_TAG))
        } catch (e: JSONException) {
            return null
        }

    }

    fun clear() {
        stops.clear()
    }

    operator fun get(position: Int): FavouriteStop {
        return stops[position]
    }
}
