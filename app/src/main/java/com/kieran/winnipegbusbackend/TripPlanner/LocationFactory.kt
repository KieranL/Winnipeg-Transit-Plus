package com.kieran.winnipegbusbackend.TripPlanner

import com.kieran.winnipegbusbackend.TripPlanner.classes.Address
import com.kieran.winnipegbusbackend.TripPlanner.classes.Intersection
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.TripPlanner.classes.Monument
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop

import org.json.JSONException
import org.json.JSONObject

object LocationFactory {

    val MONUMENT = "monument"
    val ADDRESS = "address"
    val INTERSECTION = "intersection"
    val POINT = "point"
    val STOP = "stop"

    @Throws(InvalidLocationException::class)
    fun createLocation(location: JSONObject): Location {
        var location = location
        try {
            if (location.has("origin"))
                location = location.getJSONObject("origin")
            else if (location.has("destination"))
                location = location.getJSONObject("destination")

            if (location.has("type")) {
                val type = location.getString("type")
                when (type) {
                    STOP -> return Stop(location)
                    MONUMENT -> return Monument(location)
                    ADDRESS -> return Address(location)
                    INTERSECTION -> return Intersection(location)
                    POINT -> return Location(location)
                    else -> {
                    }
                }
            }

            when {
                location.has(STOP) -> return Stop(location.getJSONObject(STOP))
                location.has(MONUMENT) -> return Monument(location.getJSONObject(MONUMENT))
                location.has(ADDRESS) -> return Address(location.getJSONObject(ADDRESS))
                location.has(INTERSECTION) -> return Intersection(location.getJSONObject(INTERSECTION))
                location.has(POINT) -> return Location(location.getJSONObject(POINT))
                else -> {
                }
            }
        } catch (e: JSONException) {

        }

        throw InvalidLocationException()
    }
}

class InvalidLocationException : Exception()
