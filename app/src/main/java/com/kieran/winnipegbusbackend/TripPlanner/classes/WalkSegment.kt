package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONObject

class WalkSegment(segment: JSONObject) : Segment(segment) {

    override fun toString(): String {
        return if (from != null && to != null)
            super.toString()
        else
            "Walk"
    }
}
