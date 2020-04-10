package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

data class WinnipegTransitRouteIdentifier(val routeNumber: String) : RouteIdentifier {
    override fun toString(): String {
        return routeNumber
    }

    override fun toShortString(): String {
        return shortenedRouteNumbers[routeNumber] ?: routeNumber
    }

    override operator fun compareTo(other: Any): Int {
        return routeNumber.compareTo((other as WinnipegTransitRouteIdentifier).routeNumber)
    }

    override fun toDataString(): String {
        return routeNumber
    }

    companion object {
        val shortenedRouteNumbers = mapOf("BLUE" to "BLU")
    }
}
