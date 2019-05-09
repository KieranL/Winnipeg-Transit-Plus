package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

object AgencySpecificClassFactory {
    fun createStopIdentifier(agencyId: Long, identifierString: String): StopIdentifier? {
        return try {
            when(agencyId){
            1L -> WinnipegTransitStopIdentifier(identifierString.toInt())
            else -> null
        }
        }catch (ex: Exception){
            null
        }
    }
}