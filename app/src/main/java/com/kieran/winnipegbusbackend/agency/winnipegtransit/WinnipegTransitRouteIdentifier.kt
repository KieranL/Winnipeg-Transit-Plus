package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

data class WinnipegTransitRouteIdentifier(val routeNumber: Int) : RouteIdentifier {
    override fun toString(): String {
        return routeNumber.toString()
    }

    override operator fun compareTo(other: Any): Int {
        return routeNumber - (other as WinnipegTransitRouteIdentifier).routeNumber
    }
}
