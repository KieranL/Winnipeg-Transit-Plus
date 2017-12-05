package com.kieran.winnipegbusbackend.TripPlanner.classes

import org.json.JSONException
import org.json.JSONObject

import java.util.Locale

class Monument @Throws(JSONException::class)
constructor(location: JSONObject) : Address(location.getJSONObject("address")) {
    internal var key: Int = 0

    override val urlString: String
        get() = String.format(Locale.CANADA, "monuments/%d", key)

    init {

        key = location.getInt("key")
        title = location.getString("name")
    }
}
