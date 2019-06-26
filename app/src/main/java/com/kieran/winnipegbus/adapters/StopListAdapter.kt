package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouriteStopsList
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType

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

            row.tag = holder
        } else {
            holder = row.tag as StopHolder
        }

        val favouriteStop: Stop = stops[position]

        holder.stopNumber!!.text = favouriteStop.identifier.toString()
        holder.stopName!!.text = favouriteStop.displayName

        return row
    }

    internal class StopHolder {
        var stopNumber: TextView? = null
        var stopName: TextView? = null
    }
}
