package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface RouteIdentifier : Serializable, Comparable<Any> {
    override fun toString(): String

    override operator fun compareTo(other: Any): Int

    override fun equals(other: Any?): Boolean
}