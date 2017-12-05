package com.kieran.winnipegbusbackend.UpcomingStops

import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.RouteKey

import java.util.ArrayList

interface UpcomingStopsManager {
    fun GetUpcomingStopsAsync(key: RouteKey, stopOnRoute: Int, listener: OnUpcomingStopsFoundListener)

    interface OnUpcomingStopsFoundListener {
        fun OnUpcomingStopsFound(stops: LoadResult<ArrayList<Int>>)
    }
}
