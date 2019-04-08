package com.kieran.winnipegbusbackend

import java.io.Serializable

open class Stop : Serializable {
    var name: String
        protected set
    var number: Int = 0
        protected set

    constructor() {
        name = ""
        number = 0
    }

    constructor(stopName: String, stopNumber: Int) {
        this.name = stopName
        this.number = stopNumber
    }

    constructor(stopNumber: Int) {
        this.number = stopNumber
        name = ""
    }

    constructor(stop: Stop) {
        name = stop.name
        number = stop.number
    }

    override fun toString(): String {
        return Integer.toString(number) + " " + name
    }
}
