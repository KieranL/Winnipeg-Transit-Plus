package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.enums.CoverageTypes

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

open class Route : Comparable<Any>, Serializable {

    var routeNumber: Int = 0
        protected set
    var routeName: String? = null
        protected set
    lateinit var coverageType: CoverageTypes
        protected set

    val isDownTownSpirit: Boolean
        get() = isDownTownSpirit(routeNumber)

    constructor(route: Route) {
        routeNumber = route.routeNumber
        routeName = route.routeName
    }

    constructor(jsonObject: JSONObject) {
        var jsonObject = jsonObject
        try {
            jsonObject = jsonObject.getJSONObject(ROUTE_TAG)

            coverageType = CoverageTypes.getEnum(jsonObject.getString(ROUTE_COVERAGE_TAG))
            routeName = jsonObject.getString(ROUTE_NAME_TAG)
            routeNumber = jsonObject.getInt(ROUTE_NUMBER_TAG)
        } catch (e: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    override fun toString(): String {
        return routeNumber.toString() + " " + if (routeName != null) routeName else ""
    }

    override operator fun compareTo(another: Any): Int {
        return routeNumber - (another as Route).routeNumber
    }

    companion object {
        private val ROUTE_TAG = "route"
        private val ROUTE_COVERAGE_TAG = "coverage"
        private val ROUTE_NUMBER_TAG = "number"
        private val ROUTE_NAME_TAG = "name"
        val DT_SPIRIT_MAX_RT_NUM = 10

        fun isDownTownSpirit(routeNumber: Int): Boolean {
            return routeNumber < DT_SPIRIT_MAX_RT_NUM
        }
    }
}
