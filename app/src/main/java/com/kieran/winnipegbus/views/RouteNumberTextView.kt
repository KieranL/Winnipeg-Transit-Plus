package com.kieran.winnipegbus.views

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.Route
import com.kieran.winnipegbusbackend.ScheduledStop
import com.kieran.winnipegbusbackend.enums.CoverageTypes

class RouteNumberTextView : TextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun setColour(scheduledStop: ScheduledStop) {
        val coverageType = scheduledStop.coverageType

        if (Route.isDownTownSpirit(scheduledStop.routeNumber)) {
            setTextColor(resources.getColor(R.color.white))
            setBackgroundResource(R.drawable.route_number_background_dt_spirit)
        } else if (coverageType == CoverageTypes.REGULAR) {
            setTextColor(resources.getColor(R.color.black))
            setBackgroundResource(R.drawable.route_number_background_regular)
        } else if (coverageType == CoverageTypes.EXPRESS || coverageType == CoverageTypes.SUPER_EXPRESS) {
            setTextColor(resources.getColor(R.color.black))
            setBackgroundResource(R.drawable.route_number_background_express)
        } else if (coverageType == CoverageTypes.RAPID_TRANSIT) {
            setTextColor(resources.getColor(R.color.white))
            setBackgroundResource(R.drawable.route_number_background_rt)
        }
    }
}
