package com.kieran.winnipegbusbackend.TripPlanner.classes

import com.kieran.winnipegbusbackend.Route

import org.json.JSONObject

class RideSegment(segment: JSONObject) : Segment(segment) {
    internal val route: Route = Route(segment)

    override fun toString(): String {
        return route.routeName.toString()
    }
}
