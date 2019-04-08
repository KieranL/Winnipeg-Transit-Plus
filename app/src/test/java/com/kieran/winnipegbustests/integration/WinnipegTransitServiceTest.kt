package com.kieran.winnipegbustests.integration

import com.kieran.winnipegbusbackend.common.StopFeatures
import com.kieran.winnipegbusbackend.common.StopTime
import com.kieran.winnipegbusbackend.enums.ScheduleType
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.common.GeoLocation
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitService
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class WinnipegTransitServiceTest : TransitServiceTest {
    @ParameterizedTest
    @MethodSource("getStopsDataSet")
    override fun testGetStopScheduleWithValidStopNumber(stop: Int, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>) {
        val transitService = WinnipegTransitService

        runBlocking {
            val stopSchedule = transitService.getStopSchedule(WinnipegTransitStopIdentifier(stop), startTime, endTime, routes)

            Assertions.assertNotNull(stopSchedule)
            Assertions.assertNotNull(stopSchedule.getLatLng())
        }
    }

    @ParameterizedTest
    @MethodSource("findStopsDataSet")
    override fun testFindStopsThatExistByName(name: String, count: Int) {
        val transitService = WinnipegTransitService

        runBlocking {
            val stops = transitService.findStop(name)

            Assertions.assertEquals(count, stops.size)
        }
    }

    @ParameterizedTest
    @MethodSource("getRouteStopsDataSet")
    override fun testGetRouteStopsWithValidRouteNumbers(routeNumber: Int, stopCount: Int) {
        val transitService = WinnipegTransitService

        runBlocking {
            val routeStops = transitService.getRouteStops(WinnipegTransitRouteIdentifier(routeNumber))
            Assertions.assertEquals(stopCount, routeStops.size)
        }
    }

    @ParameterizedTest
    @MethodSource("getStopDetailsDataSet")
    override fun testGetStopDetailsWithValidStopNumbers(stopNumber: Int, stopCount: Int) {
        val transitService = WinnipegTransitService

        runBlocking {
            val routeStops = transitService.getStopDetails(WinnipegTransitStopIdentifier(stopNumber), StopFeatures(WinnipegTransitStopIdentifier(stopNumber), "test", null))
            Assertions.assertEquals(stopCount, routeStops.getStopFeatures().size)
        }
    }

    @ParameterizedTest
    @MethodSource("getNearbyStopsDataSet")
    override fun testGetNearbyStopsWithValidLocation(latitude: Double, longitude: Double, distance: Int, stopCount: Int) {
        val transitService = WinnipegTransitService

        runBlocking {
            val routeStops = transitService.findClosestStops(GeoLocation(latitude, longitude), distance)
            Assertions.assertEquals(stopCount, routeStops.size)
        }
    }

    @Test
    override fun testGetSupportedFeatures() {
        val transitService = WinnipegTransitService

        val features = transitService.supportedFeatures()
        Assertions.assertNotNull(features)
    }

    @Test
    override fun testGetLastQueryTime() {
        val transitService = WinnipegTransitService

        Assertions.assertNotNull(transitService.getLastQueryTime())
    }

    @Test
    override fun testGetScheduleType() {
        val transitService = WinnipegTransitService
        Assertions.assertEquals(ScheduleType.LIVE, transitService.getScheduleType())
    }

    @Test
    @Disabled
    override fun testGetUpcomingStops() {
        TODO("Figure out if this can be tested without requiring other service calls") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        @JvmStatic
        fun findStopsDataSet() = listOf(
                Arguments.of("Bell MTS Place", 3),
                Arguments.of("Wpg Square", 2),
                Arguments.of("Polo Park", 11),
                Arguments.of("NB Southwest Transitway at Harkness", 1),
                Arguments.of("ADCDE", 0)
        )

        @JvmStatic
        fun getStopsDataSet() = listOf(
                Arguments.of(10545, null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(60105, null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(40195, null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(20072, null, null, ArrayList<RouteIdentifier>())
        )

        @JvmStatic
        fun getRouteStopsDataSet() = listOf(
                Arguments.of(160, 78),
                Arguments.of(16, 352),
                Arguments.of(77, 285),
                Arguments.of(66, 169)
        )

        @JvmStatic
        fun getStopDetailsDataSet() = listOf(
                Arguments.of(10545, 3),
                Arguments.of(60105, 2),
                Arguments.of(40195, 3),
                Arguments.of(20072, 0)
        )

        @JvmStatic
        fun getNearbyStopsDataSet() = listOf(
                Arguments.of(49.8954, -97.1385, 1000, 151),
                Arguments.of(49.8954, -97.1385, 100, 3),
                Arguments.of(49.8854, -97.1285, 500, 23),
                Arguments.of(49.9054, -97.1585, 250, 6),
                Arguments.of(50.9054, -96.1585, 1000, 0)
        )
    }
}