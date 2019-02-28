package com.kieran.winnipegbustests.integration

import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import com.kieran.winnipegbusbackend.winnipegtransit.WinnipegTransitService
import com.kieran.winnipegbusbackend.winnipegtransit.WinnipegTransitStopIdentifier
import kotlinx.coroutines.runBlocking

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class WinnipegTransitServiceTest {
    @ParameterizedTest
    @MethodSource("findStopsDataSet")
    fun testFindStopsThatExistByName(name: String, count: Int) {
        val transitService = WinnipegTransitService

        runBlocking {
            val stops = transitService.findStop(name)

            Assertions.assertEquals(count, stops.size)
        }
    }

    @ParameterizedTest
    @MethodSource("getStopsDataSet")
    fun testGetStopSchedule(stop: StopIdentifier, startTime: StopTime?, endTime: StopTime?, routes: List<RouteIdentifier>) {
        val transitService = WinnipegTransitService

        runBlocking {
            val stopSchedule = transitService.getStopSchedule(stop, startTime, endTime, routes)

            Assertions.assertNotNull(stopSchedule)
            Assertions.assertNotNull(stopSchedule.getLatLng())
        }
    }

    companion object {
        @JvmStatic
        fun findStopsDataSet() = listOf(
                Arguments.of("Bell MTS Place", 3),
                Arguments.of("Wpg Square", 2),
                Arguments.of("Polo Park", 11),
                Arguments.of("NB Southwest Transitway at Harkness", 1)
        )

        @JvmStatic
        fun getStopsDataSet() = listOf(
                Arguments.of(WinnipegTransitStopIdentifier(10545), null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(WinnipegTransitStopIdentifier(60105), null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(WinnipegTransitStopIdentifier(40195), null, null, ArrayList<RouteIdentifier>()),
                Arguments.of(WinnipegTransitStopIdentifier(20072), null, null, ArrayList<RouteIdentifier>())
        )
    }
}