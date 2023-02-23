package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.exceptions.AgencyNotFoundException
import com.kieran.winnipegbusbackend.exceptions.InvalidRouteIdentifierException
import com.kieran.winnipegbusbackend.exceptions.InvalidStopIdentifierException
import com.kieran.winnipegbusbackend.interfaces.Logger
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

object AgencySpecificClassFactory {
    fun createStopIdentifier(agencyId: Long, identifierString: String): StopIdentifier {
        return try {
            when (agencyId) {
                2L -> WinnipegTransitStopIdentifier(identifierString.toInt())
                else -> throw AgencyNotFoundException()
            }
        } catch (ex: NumberFormatException) {
            Logger.getLogger().error(ex, "Invalid stop identifier")
            throw InvalidStopIdentifierException()
        }
    }

    fun createRouteIdentifier(agencyId: Long, text: String): RouteIdentifier {
        return try {
            when (agencyId) {
                2L -> WinnipegTransitRouteIdentifier(text)
                else -> throw AgencyNotFoundException()
            }
        } catch (ex: NumberFormatException) {
            Logger.getLogger().error(ex, "Invalid route identifier")
            throw InvalidRouteIdentifierException()
        }
    }
}