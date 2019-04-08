package com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes

import com.kieran.winnipegbusbackend.Route

import org.json.JSONObject

class RideSegment(tripParameters: TripParameters, segment: JSONObject) : Segment(tripParameters, segment) {
//    internal val route: Route = Route(segment)

    override fun toString(): String {
        return ""
//        return "Ride " + route.routeName.toString() + " to " + to?.title
    }
}
