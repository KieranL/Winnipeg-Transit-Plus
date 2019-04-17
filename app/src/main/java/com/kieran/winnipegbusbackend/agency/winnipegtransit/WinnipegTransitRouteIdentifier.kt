package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

class WinnipegTransitRouteIdentifier(val routeNumber: Int) : RouteIdentifier {
    override fun equals(other: Any?): Boolean {
        if (other == null)
            return false

        return compareTo(other) == 0
    }

    override fun toString(): String {
        return routeNumber.toString()
    }

    override operator fun compareTo(other: Any): Int {
        return routeNumber - (other as WinnipegTransitRouteIdentifier).routeNumber
    }
}
