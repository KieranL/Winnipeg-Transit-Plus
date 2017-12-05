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

    fun createLocation(location: JSONObject): Location? {
        var location = location
        try {
            if (location.has("origin"))
                location = location.getJSONObject("origin")
            else if (location.has("destination"))
                location = location.getJSONObject("destination")

            if (location.has("type")) {
                val type = location.getString("type")
                if (type == STOP)
                    return Stop(location)
                else if (type == MONUMENT)
                    return Monument(location)
                else if (type == ADDRESS)
                    return Address(location)
                else if (type == INTERSECTION)
                    return Intersection(location)
                else if (type == POINT)
                    return Location(location)
            }

            if (location.has(STOP))
                return Stop(location.getJSONObject(STOP))
            else if (location.has(MONUMENT))
                return Monument(location.getJSONObject(MONUMENT))
            else if (location.has(ADDRESS))
                return Address(location.getJSONObject(ADDRESS))
            else if (location.has(INTERSECTION))
                return Intersection(location.getJSONObject(INTERSECTION))
            else if (location.has(POINT))
                return Location(location.getJSONObject(POINT))
        } catch (e: JSONException) {

        }

        return null
    }
}
