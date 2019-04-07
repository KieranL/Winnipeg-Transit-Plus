package com.kieran.winnipegbustests.integration

import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

interface TransitServiceTest {
    @ParameterizedTest
    @MethodSource("getStopsDataSet")
    fun testGetStopScheduleWithValidStopNumber(stop: Int, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>)

    @ParameterizedTest
    @MethodSource("getStopDetailsDataSet")
    fun testGetStopDetailsWithValidStopNumbers(stopNumber: Int, stopCount: Int)

    @ParameterizedTest
    @MethodSource("getRouteStopsDataSet")
    fun testGetRouteStopsWithValidRouteNumbers(routeNumber: Int, stopCount: Int)

    @ParameterizedTest
    @MethodSource("findStopsDataSet")
    fun testFindStopsThatExistByName(name: String, count: Int)

    @ParameterizedTest
    @MethodSource("getNearbyStopsDataSet")
    fun testGetNearbyStopsWithValidLocation(latitude: Double, longitude: Double, distance: Int, stopCount: Int)

    @Test
    fun testGetUpcomingStops()

    @Test
    fun testGetLastQueryTime()

    @Test
    fun testGetScheduleType()

    @Test
    fun testGetSupportedFeatures()
}