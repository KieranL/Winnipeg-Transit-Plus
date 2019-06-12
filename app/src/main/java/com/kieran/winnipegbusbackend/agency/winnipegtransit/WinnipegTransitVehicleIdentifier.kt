package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.VehicleIdentifier

data class WinnipegTransitVehicleIdentifier(val busNumber: Int) : VehicleIdentifier {
    override fun toString(): String {
        return busNumber.toString()
    }
}