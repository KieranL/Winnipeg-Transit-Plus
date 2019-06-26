package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.Location
import java.io.Serializable

class GeoLocation(val latitude: Double, val longitude: Double) : Location, Serializable