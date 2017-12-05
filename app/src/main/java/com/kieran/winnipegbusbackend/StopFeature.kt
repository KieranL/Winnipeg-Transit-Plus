package com.kieran.winnipegbusbackend

import org.json.JSONException
import org.json.JSONObject

import java.io.Serializable

class StopFeature : Serializable {
    var count: Int = 0
        private set
    var name: String? = null
        private set

    constructor(count: Int, name: String) {
        this.count = count
        this.name = name
    }

    constructor(featureNode: JSONObject) {
        try {
            name = featureNode.getString(NAME_TAG)
            count = featureNode.getInt(COUNT_TAG)
        } catch (e: JSONException) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }

    }

    companion object {
        private val NAME_TAG = "name"
        private val COUNT_TAG = "count"
    }
}
