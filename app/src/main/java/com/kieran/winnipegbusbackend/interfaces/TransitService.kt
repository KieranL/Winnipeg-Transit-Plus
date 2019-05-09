package com.kieran.winnipegbusbackend.interfaces

import com.kieran.winnipegbusbackend.common.StopSchedule
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.StopFeatures
import com.kieran.winnipegbusbackend.common.StopTime
import com.kieran.winnipegbusbackend.common.UpcomingStop
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.enums.SearchQueryType
import com.kieran.winnipegbusbackend.enums.SupportedFeature

interface TransitService {
    suspend fun getStopSchedule(stop: StopIdentifier, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier> = ArrayList()): StopSchedule

    suspend fun getStopDetails(stop: StopIdentifier, stopFeatures: StopFeatures): StopFeatures

    suspend fun getRouteStops(route: RouteIdentifier): List<FavouriteStop>

    suspend fun findStop(name: String): List<FavouriteStop>

    suspend fun findClosestStops(location: Location, distance: Int = 1000, stopCount: Int = 1): List<FavouriteStop>

    suspend fun getUpcomingStops(key: TripIdentifier, scheduledStopKey: ScheduledStopKey, after: StopTime): List<UpcomingStop>

    fun getLastQueryTime(): StopTime

    fun getScheduleType(): ScheduleType

    fun supportedFeatures(): List<SupportedFeature>

    fun getSearchQueryType(searchText: String): SearchQueryType

    fun parseStringToStopIdentifier(text: String): StopIdentifier

    fun parseStringToRouteIdentifier(text: String): RouteIdentifier

    fun getAgencyId(): Int
}