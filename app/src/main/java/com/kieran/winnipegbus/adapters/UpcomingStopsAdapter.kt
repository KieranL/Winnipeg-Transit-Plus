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
        var row = row
        val holder: StopHolder?

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false)

            holder = StopHolder()

            if(row != null) {
                holder.stopNumber = row.findViewById<View>(R.id.upcoming_stop_number) as TextView
                holder.stopName = row.findViewById<View>(R.id.upcoming_stop_name) as TextView
                holder.time = row.findViewById<View>(R.id.upcoming_stop_time) as TextView

                row.tag = holder
            }else {
                row = View(context)
                row.tag = holder
                return row
            }
        } else {
            holder = row.tag as StopHolder?
        }

        val upcomingStop = upComingStops[position]
        holder?.stopNumber?.text = upcomingStop.identifier.toString()
        holder?.stopName?.text = upcomingStop.name
        holder?.time?.text = upcomingStop.time.toFormattedString(null, use24hrTime)
        return row
    }

    class StopHolder {
        internal var time: TextView? = null
        internal var stopName: TextView? = null
        internal var stopNumber: TextView? = null
    }
}
