package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.RouteBadge
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

data class WinnipegTransitRouteIdentifier(val routeNumber: String, private var routeBadge: RouteBadge?) : RouteIdentifier {
    override fun getRouteBadge(): RouteBadge? {
        return routeBadge
    }

    override fun setBadge(routeBadge: RouteBadge) {
        this.routeBadge = routeBadge
    }

    override fun toString(): String {
        return routeNumber
    }

    override fun toShortString(): String {
        return shortenedRouteNumbers[routeNumber] ?: routeNumber
    }

    override operator fun compareTo(other: Any): Int {
        return routeNumber.compareTo((other as WinnipegTransitRouteIdentifier).routeNumber)
    }

    override operator fun equals(other: Any?): Boolean {
        return routeNumber == (other as WinnipegTransitRouteIdentifier).routeNumber
    }

    override fun toDataString(): String {
        return routeNumber
    }

    companion object {
        val shortenedRouteNumbers = mapOf("BLUE" to "BLU")
    }

    override fun hashCode(): Int {
        return routeNumber.hashCode()
    }
}
