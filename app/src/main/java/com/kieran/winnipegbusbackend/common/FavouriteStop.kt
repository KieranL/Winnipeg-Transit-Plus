package com.kieran.winnipegbusbackend.common

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

import java.io.Serializable

class FavouriteStop : Stop, Serializable {
    var timesUsed: Int = 0
        private set
    @Transient
    var latLng: LatLng? = null //TODO seperate this somehow?
    var alias: String? = null
        set(alias) = if (alias == name)
            field = null
        else
            field = alias

    val displayName: String
        get() = if (this.alias == null) name else this.alias!!

    constructor(stopName: String, stopIdentifier: StopIdentifier, timesUsed: Int) : super(stopName, stopIdentifier) {
        this.timesUsed = timesUsed
    }

    constructor(stopName: String, stopIdentifier: StopIdentifier) : super(stopName, stopIdentifier) {
        this.timesUsed = 0
    }

    fun use() {
        timesUsed++
    }
}
