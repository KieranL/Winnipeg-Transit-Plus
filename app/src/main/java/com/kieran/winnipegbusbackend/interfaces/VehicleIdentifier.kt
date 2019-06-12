package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface VehicleIdentifier : Serializable {
    override fun toString(): String

    override fun equals(other: Any?): Boolean
}