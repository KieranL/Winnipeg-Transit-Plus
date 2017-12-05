package com.kieran.winnipegbusbackend

import java.io.Serializable

class ScheduledStopKey(key: String) : Serializable {
    val busKey: Int
    val stopNumber: Int

    init {
        val keys = key.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        //substring is needed for unexpected characters in some keys, i.e. some keys for Route 75: 7191450NNYNYNN-2
        busKey = Integer.parseInt(keys[0].substring(0, 7))
        stopNumber = Integer.parseInt(keys[1])
    }

    override fun toString(): String {
        return "Key: $busKey stop on route: $stopNumber"
    }

    fun equals(another: ScheduledStopKey): Boolean {
        return busKey == another.busKey
    }
}