package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.kieran.winnipegbus.activities.BaseActivity
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.views.RouteNumberTextView
import com.kieran.winnipegbusbackend.ScheduledStop
import com.kieran.winnipegbusbackend.TransitApiManager

class StopTimeAdapter(context: Context, private val layoutResourceId: Int, private val scheduledStops: List<ScheduledStop>) : ArrayAdapter<ScheduledStop>(context, layoutResourceId, scheduledStops) {
    private var use24hrTime: Boolean = false
    private val inflater: LayoutInflater

    init {
        inflater = (context as Activity).layoutInflater

        loadTimeSetting()
    }

    override fun getView(position: Int, row: View?, parent: ViewGroup): View {
        var row = row
        val holder: StopTimeHolder

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false)

            holder = StopTimeHolder()
            holder.routeNumber = row!!.findViewById<View>(R.id.route_number_text) as RouteNumberTextView
            holder.routeVariantName = row.findViewById<View>(R.id.route_name_text) as TextView
            holder.timeStatus = row.findViewById<View>(R.id.time_status_text) as TextView
            holder.departureTime = row.findViewById<View>(R.id.departure_time_text) as TextView

            row.tag = holder
        } else {
            holder = row.tag as StopTimeHolder
        }

        val scheduledStop = scheduledStops[position]
        holder.routeNumber!!.text = Integer.toString(scheduledStop.routeNumber)
        holder.routeNumber!!.setColour(scheduledStop)
        holder.routeVariantName!!.text = scheduledStop.routeVariantName
        holder.timeStatus!!.text = scheduledStop.timeStatus
        holder.departureTime!!.text = scheduledStop.estimatedDepartureTime!!.toFormattedString(TransitApiManager.lastQueryTime, use24hrTime)

        return row
    }

    fun loadTimeSetting() {
        use24hrTime = BaseActivity.getTimeSetting(context)
    }

    private class StopTimeHolder {
        internal var routeNumber: RouteNumberTextView? = null
        internal var routeVariantName: TextView? = null
        internal var timeStatus: TextView? = null
        internal var departureTime: TextView? = null
    }
}
