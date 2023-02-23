package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

class RecentStop(stopName: String, stopIdentifier: StopIdentifier) : Stop(stopName, stopIdentifier, null) {

    override fun equals(other: Any?): Boolean
    {
       return identifier == (other as RecentStop).identifier
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}