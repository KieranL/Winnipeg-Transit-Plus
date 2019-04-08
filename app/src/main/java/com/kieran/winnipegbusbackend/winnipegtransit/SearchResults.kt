package com.kieran.winnipegbusbackend.winnipegtransit

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.Stop
import com.kieran.winnipegbusbackend.StopSchedule

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class SearchResults {
    private val stops: ArrayList<FavouriteStop> = ArrayList()

    val length: Int
        get() = stops.size

    fun loadStops(result: LoadResult<JSONObject>): SearchResults {
        if (result.result != null) {
            stops.clear()

            try {
                val stops = result.result.getJSONArray(Stop.STOP_TAG)

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
        return try {
            val geographic = stop.getJSONObject(Stop.STOP_CENTRE_TAG).getJSONObject(Stop.GEOGRAPHIC_TAG)
            LatLng(geographic.getDouble(StopSchedule.LATITUDE_TAG), geographic.getDouble(StopSchedule.LONGITUDE_TAG))
        } catch (e: JSONException) {
            null
        }

    }

    fun clear() {
        stops.clear()
    }

    operator fun get(position: Int): FavouriteStop {
        return stops[position]
    }
}