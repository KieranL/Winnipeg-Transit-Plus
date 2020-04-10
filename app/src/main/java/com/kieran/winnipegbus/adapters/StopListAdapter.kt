package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.views.RouteNumberTextView
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.enums.CoverageTypes
import kotlin.math.round

class StopListAdapter(context: Context, private var layoutResourceId: Int, private var stops: List<Stop>) : ArrayAdapter<Stop>(context, layoutResourceId, stops) {
    private var inflater: LayoutInflater? = null

    init {
        inflater = (context as Activity).layoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val holder: StopHolder

        if (row == null) {
            row = inflater!!.inflate(layoutResourceId, parent, false)

            holder = StopHolder()
            holder.stopNumber = row!!.findViewById<View>(R.id.favourites_stop_number) as TextView
            holder.stopName = row.findViewById<View>(R.id.favourites_stop_name) as TextView
            holder.routeNumbers = row.findViewById<View>(R.id.favourite_filter_stop_numbers) as FlexboxLayout

            row.tag = holder
        } else {
            holder = row.tag as StopHolder
        }

        val favouriteStop: Stop = stops[position]

        holder.stopNumber!!.text = favouriteStop.identifier.toString()
        holder.stopName!!.text = favouriteStop.displayName
        holder.routeNumbers?.removeAllViews()

        if(favouriteStop.routes != null) {
            for (route in favouriteStop.routes!!) {
                val view = inflater?.inflate(R.layout.route_number, null) as RouteNumberTextView
                view.text = route.toShortString()
                view.setColour(route, CoverageTypes.REGULAR)

                holder.routeNumbers?.addView(view)
            }
        }

        return row
    }

    internal class StopHolder {
        var stopNumber: TextView? = null
        var stopName: TextView? = null
        var routeNumbers: FlexboxLayout? = null
    }
}
