package com.kieran.winnipegbustests.unit

import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.ListRecentStopsService
import com.kieran.winnipegbusbackend.common.RecentStop
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class EphemeralRecentStopsServiceTest {
    @Test
    fun testUseWithEmptyList() {
        val recentStopsService = ListRecentStopsService
        recentStopsService.reset()
        val stopId = AgencySpecificClassFactory.createStopIdentifier(2, "12345")
        recentStopsService.use(RecentStop("One", stopId))

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(1, recentStops.size)
        Assertions.assertEquals("12345", recentStops.first().toString())
    }

    @Test
    fun testUseWithSingleEntryPutsRecentAtFront() {
        val recentStopsService = ListRecentStopsService
        recentStopsService.reset()
        val stop1 = AgencySpecificClassFactory.createStopIdentifier(2, "1")
        val stop2 = AgencySpecificClassFactory.createStopIdentifier(2, "2")
        recentStopsService.use(RecentStop("One", stop1))
        recentStopsService.use(RecentStop("two", stop2))

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(2, recentStops.size)
        Assertions.assertEquals("2", recentStops.first().toString())
    }

    @Test
    fun testUseWithStopAlreadyInListPutsAtFrontAndDoesNotDuplicate() {
        val recentStopsService = ListRecentStopsService
        recentStopsService.reset()
        val stop1 = AgencySpecificClassFactory.createStopIdentifier(2, "1")
        val stop2 = AgencySpecificClassFactory.createStopIdentifier(2, "2")
        recentStopsService.use(RecentStop("One", stop1))
        recentStopsService.use(RecentStop("two", stop2))
        recentStopsService.use(RecentStop("One", stop1))

        val recentStops = recentStopsService.getRecentStops()

        Assertions.assertEquals(2, recentStops.size)
        Assertions.assertEquals("1", recentStops.first().toString())
    }
}