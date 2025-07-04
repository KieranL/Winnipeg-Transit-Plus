package com.kieran.winnipegbus.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitRouteIdentifier
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitService
import com.kieran.winnipegbusbackend.enums.CoverageTypes
import com.kieran.winnipegbusbackend.interfaces.RouteBadge
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import kotlin.math.round

class RouteNumberTextView : TextView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun setColour(routeBadge: RouteBadge?) {
        if (routeBadge == null)
            return

        setTextColor(Color.parseColor(routeBadge.getTextColour()))
        setBackgroundColor(Color.parseColor(routeBadge.getBackgroundColour()))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (textSize < spToPx(40)) {
            val width = spToPx(36)
            val height = spToPx(24)
            setMeasuredDimension(width, height)
        }
    }

    private fun spToPx(dp: Int): Int {
        val density = context.resources
                .displayMetrics
                .scaledDensity
        return round(dp.toFloat() * density).toInt()
    }

}
