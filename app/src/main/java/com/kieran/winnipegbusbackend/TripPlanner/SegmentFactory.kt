package com.kieran.winnipegbusbackend.TripPlanner

import com.kieran.winnipegbusbackend.TripPlanner.classes.RideSegment
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment
import com.kieran.winnipegbusbackend.TripPlanner.classes.TransferSegment
import com.kieran.winnipegbusbackend.TripPlanner.classes.WalkSegment

import org.json.JSONException
import org.json.JSONObject

object SegmentFactory {
    fun createSegment(segment: JSONObject): Segment? {
        return try {
            when (segment.getString("type")) {
                "walk" -> WalkSegment(segment)
                "ride" -> RideSegment(segment)
                "transfer" -> TransferSegment(segment)
                else -> null
            }
        } catch (e: JSONException) {
            null
        }

    }
}
