package com.kieran.winnipegbusbackend.TripPlanner

import com.kieran.winnipegbusbackend.TripPlanner.classes.*

import org.json.JSONException
import org.json.JSONObject

object LocationFactory {

    val MONUMENT = "monument"
    val ADDRESS = "address"
    val INTERSECTION = "intersection"
    val POINT = "point"
    val STOP = "stop"

    @Throws(InvalidLocationException::class)
    fun createLocation(tripParameters: TripParameters, location: JSONObject): Location {
        var location = location
        var title = "Location"
        try {
            if (location.has("origin")) {
                location = location.getJSONObject("origin")
                title = tripParameters.origin?.title!!
            } else if (location.has("destination")) {
                location = location.getJSONObject("destination")
                title = tripParameters.destination?.title!!
            }

            when {
                location.has(STOP) -> return StopLocation(location.getJSONObject(STOP))
                location.has(MONUMENT) -> return Monument(location.getJSONObject(MONUMENT))
                location.has(ADDRESS) -> return Address(location.getJSONObject(ADDRESS))
                location.has(INTERSECTION) -> return Intersection(location.getJSONObject(INTERSECTION))
                location.has(POINT) -> return Location(location.getJSONObject(POINT), title)
                else -> {
                }
            }
        } catch (e: JSONException) {

        }

        throw InvalidLocationException()
    }

    fun createLocation(location: JSONObject): Location {
        try {
            val type = location.getString("type")
            when (type) {
                STOP -> return StopLocation(location)
                MONUMENT -> return Monument(location)
                ADDRESS -> return Address(location)
                INTERSECTION -> return Intersection(location)
                POINT -> return Location(location)
                else -> {
                }
            }
        } catch (e: JSONException) {

        }

        throw InvalidLocationException()
    }
}

class InvalidLocationException : Exception()
