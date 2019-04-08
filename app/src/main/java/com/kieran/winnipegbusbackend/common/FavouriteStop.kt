package com.kieran.winnipegbusbackend.common

import com.google.android.gms.maps.model.LatLng
import com.kieran.winnipegbusbackend.Stop

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

    constructor(stopName: String, stopNumber: Int, timesUsed: Int) : super(stopName, stopNumber) {
        this.timesUsed = timesUsed
    }

    constructor(stopName: String, stopNumber: Int) : super(stopName, stopNumber) {
        this.timesUsed = 0
    }

    fun use() {
        timesUsed++
    }
}
