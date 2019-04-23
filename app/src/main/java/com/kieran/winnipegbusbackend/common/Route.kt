package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier

import java.io.Serializable

open class Route : Comparable<Any>, Serializable {
    lateinit var routeIdentifier: RouteIdentifier

    var routeName: String? = null
        protected set
    lateinit var coverageType: CoverageTypes
        protected set

    constructor()

    constructor(route: Route) {
        routeIdentifier = route.routeIdentifier
        routeName = route.routeName
        coverageType = route.coverageType
    }

    constructor(routeNumber: RouteIdentifier, routeName: String?, coverageType: CoverageTypes) {
        this.routeIdentifier = routeNumber
        this.routeName = routeName
        this.coverageType = coverageType
    }

    override fun toString(): String {
        return routeIdentifier.toString() + " " + if (routeName != null) routeName else ""
    }

    override operator fun compareTo(other: Any): Int {
        return routeIdentifier.compareTo((other as Route).routeIdentifier)
    }
}
