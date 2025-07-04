package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface RouteIdentifier : Serializable, Comparable<Any> {
    fun getRouteBadge(): RouteBadge?

    fun setBadge(routeBadge: RouteBadge)

    override fun toString(): String

    fun toShortString(): String

    override operator fun compareTo(other: Any): Int

    override fun equals(other: Any?): Boolean

    fun toDataString(): String
}