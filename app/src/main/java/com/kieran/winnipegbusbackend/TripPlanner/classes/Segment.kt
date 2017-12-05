package com.kieran.winnipegbusbackend.TripPlanner.classes

import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory

import org.json.JSONException
import org.json.JSONObject

abstract class Segment(`object`: JSONObject) {
    var times: Times
        internal set
    internal var from: Location? = null
    internal var to: Location? = null

    init {
        try {
            val times = `object`.getJSONObject("times")
            this.times = Times(times)

            from = LocationFactory.createLocation(`object`.getJSONObject("from"))
            to = LocationFactory.createLocation(`object`.getJSONObject("to"))

        } catch (ex: JSONException) {

        }

    }

    override fun toString(): String {
        return from!!.title + " to " + to!!.title
    }
}