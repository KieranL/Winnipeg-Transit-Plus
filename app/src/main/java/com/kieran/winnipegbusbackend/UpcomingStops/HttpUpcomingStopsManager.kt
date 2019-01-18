package com.kieran.winnipegbusbackend.UpcomingStops

import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.RouteKey
import com.kieran.winnipegbusbackend.winnipegtransit.TransitApiManager

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class HttpUpcomingStopsManager : UpcomingStopsManager {
    override fun GetUpcomingStopsAsync(key: RouteKey, stopOnRoute: Int, listener: UpcomingStopsManager.OnUpcomingStopsFoundListener) {
        val query = TransitApiManager.generateSearchQuery(key)
        TransitApiManager.getJsonAsync(query.queryUrl, object : TransitApiManager.OnJsonLoadResultReceiveListener {
            override fun onReceive(result: LoadResult<JSONObject>) {
                if (result.result != null) {
                    try {
                        val stops = result.result.getJSONArray("stops")
                        val stopNumbers = ArrayList<Int>()

                        for (i in 0 until stops.length()) {
                            stopNumbers.add(stops.getJSONObject(i).getInt("number"))
                        }

                        listener.OnUpcomingStopsFound(LoadResult(stopNumbers, null))
                    } catch (ex: JSONException) {
                        listener.OnUpcomingStopsFound(LoadResult<ArrayList<Int>>(null, ex))
                    }
                }
            }
        })
    }
}
