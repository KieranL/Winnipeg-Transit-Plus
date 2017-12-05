package com.kieran.winnipegbusbackend.TripPlanner

import java.io.Serializable

enum class TimeMode private constructor(var name: String, var urlParameter: String) : Serializable {
    ARRIVE_BEFORE("Arrive Before", "arrive-before"),
    ARRIVE_AFTER("Arrive After", "arrive-after"),
    DEPART_BEFORE("Depart Before", "depart-before"),
    DEPART_AFTER("Depart After", "depart-after");


    companion object {

        fun getTimeModeByName(name: String): TimeMode {
            val modes = arrayOf(ARRIVE_BEFORE, ARRIVE_AFTER, DEPART_BEFORE, DEPART_AFTER)

            for (m in modes) {
                if (m.name == name)
                    return m
            }

            return DEPART_AFTER
        }
    }

}
