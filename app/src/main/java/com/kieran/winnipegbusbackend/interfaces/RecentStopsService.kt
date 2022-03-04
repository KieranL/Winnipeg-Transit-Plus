package com.kieran.winnipegbusbackend.interfaces

interface RecentStopsService {
    fun use(stop: StopIdentifier)

    fun getRecentStops(): List<StopIdentifier>

    fun reset()
}