package com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner.classes

import org.json.JSONObject

class WalkSegment(tripParameters: TripParameters, segment: JSONObject) : Segment(tripParameters, segment) {

    override fun toString(): String {
        return if (from != null && to != null)
            "Walk from " + super.toString()
        else
            "Walk"
    }
}
