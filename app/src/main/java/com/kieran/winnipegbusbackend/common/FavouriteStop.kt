package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

import java.io.Serializable

class FavouriteStop : Stop, Serializable {
    var timesUsed: Int = 0
        private set
    var alias: String? = null
        set(alias) = if (alias == name)
            field = null
        else
            field = alias

    override val displayName: String
        get() = if (this.alias == null) name else this.alias!!

    constructor(stopName: String, stopIdentifier: StopIdentifier, timesUsed: Int) : super(stopName, stopIdentifier) {
        this.timesUsed = timesUsed
    }

    constructor(stopName: String, stopIdentifier: StopIdentifier) : super(stopName, stopIdentifier) {
        this.timesUsed = 0
    }

    constructor(name: String, identifier: StopIdentifier, timesUsed: Int = 0, latlng: GeoLocation? = null, id: Long = -1, alias: String? = null, routes: Collection<RouteIdentifier>? = null) : super(name, identifier, latlng) {
        this.timesUsed = timesUsed
        this.id = id
        this.alias = alias
        this.routes = routes
    }

    fun use() {
        timesUsed++
    }
}
