package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import java.io.Serializable

class RouteSchedule(routeNumber: RouteIdentifier, routeName: String?, coverageType: CoverageTypes, val scheduledStops: ArrayList<ScheduledStop>) : Route(routeNumber, routeName, coverageType), Serializable


