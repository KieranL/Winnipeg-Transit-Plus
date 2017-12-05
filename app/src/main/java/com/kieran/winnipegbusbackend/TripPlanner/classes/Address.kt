package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONException
import org.json.JSONObject

import java.util.Locale

open class Address(location: JSONObject) : Location(location) {
    internal open var key: Int = 0
    internal var streetNumber: Int = 0
    internal lateinit var streetName: String

    override val urlString: String
        get() = String.format(Locale.CANADA, "addresses/%d", key)

    init {

        try {
            val street = location.getJSONObject("street")

            key = location.getInt("key")
            streetNumber = location.getInt("street-number")
            streetName = street.getString("name")
            title = String.format(Locale.CANADA, "%d %s", streetNumber, streetName)
        } catch (e: JSONException) {

        }

    }
}
