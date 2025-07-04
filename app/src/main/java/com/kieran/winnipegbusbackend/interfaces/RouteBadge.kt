package com.kieran.winnipegbusbackend.interfaces

import java.io.Serializable

interface RouteBadge: Serializable {
    fun getTextColour(): String
    fun getBackgroundColour(): String
    fun getBorderColour(): String
}