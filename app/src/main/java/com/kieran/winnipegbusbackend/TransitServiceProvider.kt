package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitService
import com.kieran.winnipegbusbackend.interfaces.TransitService

object TransitServiceProvider {
    fun getTransitService(): TransitService {
        return WinnipegTransitService
    }
}