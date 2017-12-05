package com.kieran.winnipegbusbackend.TripPlanner.classes

import com.kieran.winnipegbusbackend.TripPlanner.SegmentFactory

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class Trip(trip: JSONObject) {
    var times: Times? = null
        private set
    var segments: ArrayList<Segment>
        internal set

    init {
        segments = ArrayList()
        getSegments(trip)
    }

    private fun getSegments(trip: JSONObject) {
        try {
            val times = trip.getJSONObject("times")
            this.times = Times(times)
            val segmentNodes = trip.getJSONArray("segments")

            for (i in 0 until segmentNodes.length()) {
                val segment = segmentNodes.getJSONObject(i)
                segments.add(SegmentFactory.createSegment(segment))
            }


        } catch (e: JSONException) {

        }

    }

    override fun toString(): String {
        return segments[0].times.startTime!!.to12hrTimeString() + "-" + segments[segments.size - 1].times.endTime!!.to12hrTimeString()
    }
}
