package com.kieran.winnipegbusbackend.TripPlanner

import com.kieran.winnipegbusbackend.TripPlanner.classes.RideSegment
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment
import com.kieran.winnipegbusbackend.TripPlanner.classes.TransferSegment
import com.kieran.winnipegbusbackend.TripPlanner.classes.WalkSegment

import org.json.JSONException
import org.json.JSONObject

object SegmentFactory {
    fun createSegment(segment: JSONObject): Segment? {
        try {
            when (segment.getString("type")) {
                "walk" -> return WalkSegment(segment)
                "ride" -> return RideSegment(segment)
                "transfer" -> return TransferSegment(segment)
                else -> return null
            }
        } catch (e: JSONException) {
            return null
        }

    }
}
