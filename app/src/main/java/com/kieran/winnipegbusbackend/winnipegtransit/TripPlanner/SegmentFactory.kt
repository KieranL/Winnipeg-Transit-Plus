package com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner

import com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner.classes.*

import org.json.JSONException
import org.json.JSONObject

object SegmentFactory {
    fun createSegment(tripParameters: TripParameters, segment: JSONObject): Segment? {
        return try {
            when (segment.getString("type")) {
                "walk" -> WalkSegment(tripParameters, segment)
                "ride" -> RideSegment(tripParameters, segment)
                "transfer" -> TransferSegment(tripParameters, segment)
                else -> null
            }
        } catch (e: JSONException) {
            null
        }

    }
}
