package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface ScheduledStopKey : Serializable, Comparable<Any> {
    override fun equals(other: Any?): Boolean
}