package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment
import com.kieran.winnipegbusbackend.TripPlanner.classes.TransferSegment
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip

class TripPlannerAdapter(context: Context, private val trips: List<Trip>) : BaseExpandableListAdapter() {
    private val inflater: LayoutInflater = (context as Activity).layoutInflater
    private val use24hrTime: Boolean = false

    override fun getGroupCount(): Int {
        return trips.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return trips[groupPosition].segments.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return trips[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return trips[groupPosition].segments[childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var row: View? = convertView
        val holder: TripHolder

        if (row == null) {
            row = inflater.inflate(R.layout.trip_planner_trip_row, parent, false)

            holder = TripHolder()
            holder.timeRange = row!!.findViewById<View>(R.id.trip_time_range) as TextView
            holder.totalTime = row.findViewById<View>(R.id.trip_time) as TextView

            row.tag = holder
        } else {
            holder = row.tag as TripHolder
        }

        val trip = trips[groupPosition]
        val times = trip.times
        holder.timeRange!!.text = "%s - %s".format(times!!.startTime!!.toFormattedString(null, use24hrTime), times.endTime!!.toFormattedString(null, use24hrTime))
        holder.totalTime!!.text = "%d".format(trip.times!!.totalTime)

        return row
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        var row: View? = convertView
        val holder: SegmentHolder

        if (row == null) {
            row = inflater.inflate(R.layout.trip_planner_trip_segment_row, parent, false)

            holder = SegmentHolder()
            holder.string = row!!.findViewById<View>(R.id.segment_string) as TextView
            holder.time = row.findViewById<View>(R.id.segment_time) as TextView

            row.tag = holder
        } else {
            holder = row.tag as SegmentHolder
        }

        val trip = trips[groupPosition]
        if (trip != null) {
            val segment = trip.segments[childPosition]
            if (segment != null) {
                holder.string!!.text = trips[groupPosition].segments[childPosition].toString()
                holder.time!!.text = "%d".format(segment.times.totalTime)
                holder.time!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, getDrawableIconResId(segment), 0)
            }
        }

        return row
    }

    private fun getDrawableIconResId(segment: Segment): Int {
        //        if(segment instanceof RideSegment)
        //            return R.drawable.ic_bus_dark;
        //        else if(segment instanceof WalkSegment)
        //            return R.drawable.ic_walk_dark;
        //        else
        return if (segment is TransferSegment)
            R.drawable.ic_clock_dark
        else
            R.drawable.ic_clock_dark
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    private class TripHolder {
        internal var timeRange: TextView? = null
        internal var totalTime: TextView? = null
    }

    private class SegmentHolder {
        internal var string: TextView? = null
        internal var time: TextView? = null
    }
}
