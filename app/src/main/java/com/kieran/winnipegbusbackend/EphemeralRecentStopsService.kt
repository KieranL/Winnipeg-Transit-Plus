package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.interfaces.RecentStopsService
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

object EphemeralRecentStopsService: RecentStopsService {
    private val stopIdCache = ArrayList<StopIdentifier>()

    override fun use(stop: StopIdentifier) {
        stopIdCache.remove(stop)

        this.stopIdCache.add(0, stop)
    }

    override fun getRecentStops(): List<StopIdentifier> {
       return this.stopIdCache
    }

    override fun reset() {
        stopIdCache.clear()
    }
}