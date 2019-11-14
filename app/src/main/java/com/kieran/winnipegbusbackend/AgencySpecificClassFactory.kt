package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
//import com.rollbar.android.Rollbar

object AgencySpecificClassFactory {
    fun createStopIdentifier(agencyId: Long, identifierString: String): StopIdentifier? {
        return try {
            when(agencyId){
            2L -> WinnipegTransitStopIdentifier(identifierString.toInt())
            else -> null
        }
        }catch (ex: Exception){
//            Rollbar.instance()?.error(ex, "Invalid Agency Id")
            null
        }
    }

    fun createRouteIdentifier(agencyId: Long, text: String): RouteIdentifier? {
        return try {
            when(agencyId){
                2L -> WinnipegTransitRouteIdentifier(text.toInt())
                else -> null
            }
        }catch (ex: Exception){
//            Rollbar.instance()?.error(ex, "Invalid Agency Id")
            null
        }
    }
}