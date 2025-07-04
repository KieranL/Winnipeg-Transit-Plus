package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.interfaces.RouteBadge
import java.io.Serializable

data class WinnipegTransitRouteBadge(
    private val textColour: String,
    private val borderColour: String,
    private val backgroundColour: String
) : RouteBadge, Serializable {
    override fun getTextColour(): String {
        return textColour
    }

    override fun getBackgroundColour(): String {
        return backgroundColour
    }

    override fun getBorderColour(): String {
        return borderColour
    }
}