package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.common.RecentStop
import com.kieran.winnipegbusbackend.interfaces.RecentStopsService
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

object ListRecentStopsService: RecentStopsService {
    private val stopIdCache = ArrayList<RecentStop>()

    override fun use(stop: RecentStop) {
        stopIdCache.remove(stop)

        this.stopIdCache.add(0, stop)
    }

    override fun getRecentStops(): List<RecentStop> {
       return this.stopIdCache
    }

    override fun reset() {
        stopIdCache.clear()
    }
}