package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

class WinnipegTransitStopIdentifier(val stopNumber: Int) : StopIdentifier {
    override fun toString(): String {
        return stopNumber.toString()
    }
}
