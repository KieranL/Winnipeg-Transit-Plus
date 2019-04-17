package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import java.io.Serializable

open class Stop : Serializable {
    var name: String
        protected set
    lateinit var identifier: StopIdentifier
        protected set

    constructor() {
        name = ""
    }

    constructor(stopName: String, stopIdentifier: StopIdentifier) {
        this.name = stopName
        this.identifier = stopIdentifier
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
