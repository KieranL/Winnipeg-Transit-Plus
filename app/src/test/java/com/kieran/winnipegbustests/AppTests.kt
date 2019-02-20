package com.kieran.winnipegbustests

import com.kieran.winnipegbusbackend.winnipegtransit.WinnipegTransitTripIdentifier
import com.kieran.winnipegbusbackend.StopSchedule
import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.winnipegtransit.TransitApiManager

import junit.framework.Assert

import org.junit.Test

class AppTests {
    @Test
    fun DateParseMilliseconds() {
        val time = StopTime.convertStringToStopTime("2016-07-10T12:07:30")
        Assert.assertEquals(time!!.milliseconds, 1468170450000L)
    }

    @Test
    fun RouteNumberSearchQueryGenerationShouldBeValid() {
        val url = TransitApiManager.generateSearchQuery(36).queryUrl

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }

    @Test
    fun RouteKeySearchQueryGenerationShouldBeValid() {
        val key = WinnipegTransitTripIdentifier(36, 1, 'M')
        val url = TransitApiManager.generateSearchQuery(key).queryUrl

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }

    @Test
    fun ServiceAdvisoriesUrlGenerationShouldBeValid() {
        val url = TransitApiManager.generateServiceAdvisoriesUrl()

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }


    @Test
    fun GenericSearchQueryGenerationShouldBeValid() {
        val url = TransitApiManager.generateSearchQuery("Grant").queryUrl

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }

    @Test
    fun StopFeaturesUrlGenerationShouldBeValid() {
        val url = TransitApiManager.generateStopFeaturesUrl(10545)

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }


    @Test
    fun StopNumberUrlGenerationShouldBeValid() {
        val url = TransitApiManager.generateStopNumberURL(10545, 11, StopTime(), null)

        Assert.assertNotNull(TransitApiManager.getJson(url))
    }

    @Test
    fun StopScheduleCreationShouldReturnScheduledStops() {
        val url = TransitApiManager.generateStopNumberURL(10545, 11, StopTime(), null)
        val result = TransitApiManager.getJson(url)
        val dom = result.result

        val stopSchedule = StopSchedule(dom!!)

        Assert.assertTrue(stopSchedule.scheduledStopsSorted.isNotEmpty())
    }

    @Test
    fun StopTimeShouldCreateCorrect12HourString() {
        val time = StopTime(1468606042000L)

        Assert.assertEquals(time.to12hrTimeString(), "1:07p")
    }

    @Test
    fun StopTimeShouldCreateCorrect24HourString() {
        val time = StopTime(1468606042000L)

        Assert.assertEquals(time.to24hrTimeString(), "13:07")
    }
}
