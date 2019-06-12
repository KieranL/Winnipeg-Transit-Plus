package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface StopIdentifier : Serializable, Comparable<StopIdentifier> {
    override fun toString(): String

    override fun equals(other: Any?): Boolean
}