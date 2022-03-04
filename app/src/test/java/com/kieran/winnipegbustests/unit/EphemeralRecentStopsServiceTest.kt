package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.EphemeralRecentStopsService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class EphemeralRecentStopsServiceTest {
    @Test
    fun testUseWithEmptyList() {
        val recentStopsService = EphemeralRecentStopsService
        recentStopsService.reset()
        val stopId = AgencySpecificClassFactory.createStopIdentifier(2, "12345")
        recentStopsService.use(stopId)

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(1, recentStops.size)
        Assertions.assertEquals("12345", recentStops.first().toString())
    }

    @Test
    fun testUseWithSingleEntryPutsRecentAtFront() {
        val recentStopsService = EphemeralRecentStopsService
        recentStopsService.reset()
        val stop1 = AgencySpecificClassFactory.createStopIdentifier(2, "1")
        val stop2 = AgencySpecificClassFactory.createStopIdentifier(2, "2")
        recentStopsService.use(stop1)
        recentStopsService.use(stop2)

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(2, recentStops.size)
        Assertions.assertEquals("2", recentStops.first().toString())
    }

    @Test
    fun testUseWithStopAlreadyInListPutsAtFrontAndDoesNotDuplicate() {
        val recentStopsService = EphemeralRecentStopsService
        recentStopsService.reset()
        val stop1 = AgencySpecificClassFactory.createStopIdentifier(2, "1")
        val stop2 = AgencySpecificClassFactory.createStopIdentifier(2, "2")
        recentStopsService.use(stop1)
        recentStopsService.use(stop2)
        recentStopsService.use(stop1)

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(2, recentStops.size)
        Assertions.assertEquals("1", recentStops.first().toString())
    }
}