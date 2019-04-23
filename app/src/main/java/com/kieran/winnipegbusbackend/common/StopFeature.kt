package com.kieran.winnipegbusbackend.common

import java.io.Serializable

class StopFeature(count: Int, name: String) : Serializable {
    var count: Int = count
        private set
    var name: String? = name
        private set

}
