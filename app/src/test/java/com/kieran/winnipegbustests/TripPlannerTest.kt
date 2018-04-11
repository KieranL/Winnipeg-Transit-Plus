package com.kieran.winnipegbustests

import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test

class TripPlannerTest {
    @Test
    @Throws(JSONException::class)
    fun CompleteTripParse() {
        val url = "https://api.winnipegtransit.com/v2/trip-planner.json?origin=addresses/130038&destination=addresses/203323&api-key=FTy2QN8ts293ZlhYP1t&usage=short"
        val result = TransitApiManager.getJson(url)
        val `object` = result.result
        val plans = `object`!!.getJSONArray("plans")
        val trips = arrayOfNulls<Trip>(plans.length())

        for (i in 0 until plans.length()) {
            trips[i] = Trip(plans.getJSONObject(i))
        }

        Assert.assertTrue(trips.isNotEmpty())
    }

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
