package com.kieran.winnipegbusbackend.interfaces

import com.kieran.winnipegbusbackend.common.RecentStop

interface RecentStopsService {
    fun use(stop: RecentStop)

    fun getRecentStops(): List<RecentStop>

    fun reset()
}