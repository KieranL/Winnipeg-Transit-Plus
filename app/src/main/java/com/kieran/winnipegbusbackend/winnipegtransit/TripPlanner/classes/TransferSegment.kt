package com.kieran.winnipegbusbackend.winnipegtransit.TripPlanner.classes

import org.json.JSONObject

class TransferSegment(tripParameters: TripParameters, segment: JSONObject) : Segment(tripParameters, segment) {
    override fun toString(): String {
        return "Wait for transfer at " + to?.title
    }
}

