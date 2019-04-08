package com.kieran.winnipegbusbackend.winnipegtransit.ServiceAdvisories

import com.kieran.winnipegbusbackend.common.StopTime

import java.io.Serializable
import java.util.ArrayList

class ServiceAdvisory(val title: String, val header: String, val affectedStops: List<AffectedStop>, val reroutes: List<Reroute>, val updatedAt: StopTime) : Serializable, Comparable<Any> {

    val affectedStopsStrings: List<String>
        get() {
            val strings = ArrayList<String>()

            for (stop in affectedStops) {
                strings.add(stop.affectedStop)
            }

            return strings
        }

    override operator fun compareTo(another: Any): Int {
        return -updatedAt.compareTo((another as ServiceAdvisory).updatedAt)
    }
}
