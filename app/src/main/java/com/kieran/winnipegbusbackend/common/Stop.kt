package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import java.io.Serializable

open class Stop : Serializable {
    var name: String
        protected set
    lateinit var identifier: StopIdentifier
        protected set
    var latLng: GeoLocation? = null //TODO seperate this somehow?
    var id: Long = -1

    var routes: Collection<RouteIdentifier>? = null

    open val displayName: String
        get() = name

    constructor(stopName: String, stopIdentifier: StopIdentifier, latLng: GeoLocation? = null) {
        this.name = stopName
        this.identifier = stopIdentifier
        this.latLng = latLng
    }

    constructor(stop: Stop) {
        name = stop.name
        identifier = stop.identifier
    }

    constructor(stopIdentifier: StopIdentifier) {
        this.identifier = stopIdentifier
        name = ""
    }

    override fun toString(): String {
        return identifier.toString() + " " + name
    }
}
