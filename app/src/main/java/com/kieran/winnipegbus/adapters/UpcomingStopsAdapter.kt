package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.common.UpcomingStop

class UpcomingStopsAdapter(internal var context: Context, private var layoutResourceId: Int, private var upComingStops: List<UpcomingStop>, private val use24hrTime: Boolean) : ArrayAdapter<UpcomingStop>(context, layoutResourceId, upComingStops) {
    private var inflater: LayoutInflater = (context as Activity).layoutInflater

    override fun getView(position: Int, row: View?, parent: ViewGroup): View {
        val view: View?
        val holder: StopHolder?

        if (row == null) {
            view = inflater.inflate(layoutResourceId, parent, false)

            holder = StopHolder()

            if(view != null) {
                holder.stopNumber = view.findViewById<View>(R.id.upcoming_stop_number) as TextView
                holder.stopName = view.findViewById<View>(R.id.upcoming_stop_name) as TextView
                holder.time = view.findViewById<View>(R.id.upcoming_stop_time) as TextView

                view.tag = holder
            }else {
                return defaultView(context, holder)
            }
        } else {
            view = row
            holder = view.tag as StopHolder?
        }

        val upcomingStop = upComingStops[position]

        return if(holder != null && upcomingStop != null) {
            val stopNumber = holder.stopNumber
            if (stopNumber != null && upcomingStop.identifier != null) stopNumber.text = upcomingStop.identifier.toString()

            val stopName = holder.stopName
            if (stopName != null && upcomingStop.name != null) stopName.text = upcomingStop.name

            val time = holder.time
            if (time != null && upcomingStop.time != null) time.text = upcomingStop.time.toFormattedString(null, use24hrTime)

            view
        }else {
            defaultView(context, holder)
        }
    }

    private fun defaultView(context: Context, tag: StopHolder?): View {
        val view = View(context)
        view.tag = tag
        return view
    }

    class StopHolder {
        internal var time: TextView? = null
        internal var stopName: TextView? = null
        internal var stopNumber: TextView? = null
    }
}
