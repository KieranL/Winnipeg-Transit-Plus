package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.TripIdentifier
import java.io.Serializable

class WinnipegTransitTripIdentifier : Serializable, TripIdentifier {
    var routeNumber: Int = 0
    private var routeDirection: Int = 0
    private var variant: Char = 0.toChar()

    val keyString: String
        get() {
            var key = routeNumber.toString() + "-" + routeDirection

            if (variant.toInt() != 0)
                key += "-" + variant

            return key
        }

    constructor(key: String) {
        val elements = key.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        routeNumber = Integer.parseInt(elements[0])
        routeDirection = Integer.parseInt(elements[1])

        if (elements.size > 2)
            variant = elements[2][0]
    }

    constructor(routeNumber: Int, routeDirection: Int, variant: Char) {
        this.routeNumber = routeNumber
        this.routeDirection = routeDirection
        this.variant = variant
    }

    companion object {
        private val ROUTE_KEY_TAG = "key"
    }
}