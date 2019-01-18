package com.kieran.winnipegbustests

import com.kieran.winnipegbusbackend.winnipegtransit.TransitApiManager
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip

import org.json.JSONException
import org.junit.Assert
import org.junit.Test

class TripPlannerTest {

    @Test
    @Throws(JSONException::class)
    fun GetAddressLocationFromQueryAndParse() {
        val url = TransitApiManager.generateLocationQueryUrl("1099 Grant")
        val result = TransitApiManager.getJson(url)
        val locations = result.result!!.getJSONArray("locations")
        val location = LocationFactory.createLocation(locations.getJSONObject(0))

        Assert.assertNotNull(location)
        Assert.assertTrue(location.title == "1099 GrantAve")
    }

    @Test
    @Throws(JSONException::class)
    fun GetIntersectionLocationFromQueryAndParse() {
        val url = TransitApiManager.generateLocationQueryUrl("Grant at Stafford")
        val result = TransitApiManager.getJson(url)
        val locations = result.result!!.getJSONArray("locations")
        val location = LocationFactory.createLocation(locations.getJSONObject(0))

        Assert.assertNotNull(location)
        Assert.assertTrue(location.title == "GrantAve at StaffordSt")
    }

    @Test
    @Throws(JSONException::class)
    fun GetMonumentLocationFromQueryAndParse() {
        val url = TransitApiManager.generateLocationQueryUrl("mts")
        val result = TransitApiManager.getJson(url)
        val locations = result.result!!.getJSONArray("locations")
        val location = LocationFactory.createLocation(locations.getJSONObject(0))

        Assert.assertNotNull(location)
        Assert.assertTrue(location.title == "Bell MTS Place")
    }
}
