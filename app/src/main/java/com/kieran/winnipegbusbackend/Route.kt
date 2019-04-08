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

    constructor()

    constructor(route: Route) {
        routeNumber = route.routeNumber
        routeName = route.routeName
    }

    constructor(routeNumber: Int, routeName: String?, coverageType: CoverageTypes) {
        this.routeNumber = routeNumber
        this.routeName = routeName
        this.coverageType = coverageType
    }

    override fun toString(): String {
        return routeNumber.toString() + " " + if (routeName != null) routeName else ""
    }

    override operator fun compareTo(other: Any): Int {
        return routeNumber - (other as Route).routeNumber
    }

    companion object {
        val DT_SPIRIT_MAX_RT_NUM = 10

        fun isDownTownSpirit(routeNumber: Int): Boolean {
            return routeNumber < DT_SPIRIT_MAX_RT_NUM
        }
    }
}
