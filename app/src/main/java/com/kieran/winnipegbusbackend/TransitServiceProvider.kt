package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.interfaces.TransitService
import com.kieran.winnipegbusbackend.winnipegtransit.WinnipegTransitService

object TransitServiceProvider {
    fun getTransitService(): TransitService {
        return WinnipegTransitService
    }
}