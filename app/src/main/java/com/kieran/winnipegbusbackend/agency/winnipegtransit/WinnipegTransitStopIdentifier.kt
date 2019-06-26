package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

data class WinnipegTransitStopIdentifier(val stopNumber: Int) : StopIdentifier {
    override fun compareTo(other: StopIdentifier): Int {
        return stopNumber - (other as WinnipegTransitStopIdentifier).stopNumber
    }

    override fun toString(): String {
        return stopNumber.toString()
    }
}
