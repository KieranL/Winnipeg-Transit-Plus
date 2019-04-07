package com.kieran.winnipegbusbackend.winnipegtransit.ServiceAdvisories

import java.io.Serializable

class AffectedStop(val affectedStop: String, val affectedRoutes: String) : Serializable {

    override fun toString(): String {
        return affectedStop + " " + affectedRoutes + "\n"
    }
}
