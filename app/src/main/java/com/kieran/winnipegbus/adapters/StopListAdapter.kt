package com.kieran.winnipegbus.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.FavouriteStopsList
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType

class StopListAdapter : ArrayAdapter<FavouriteStop> {
    private var layoutResourceId: Int = 0
    private var stops: List<FavouriteStop>? = null
    private var inflater: LayoutInflater? = null

    constructor(context: Context, layoutResourceId: Int) : super(context, layoutResourceId, FavouriteStopsList.getFavouriteStopsSorted(sortPreference!!)) {
        this.layoutResourceId = layoutResourceId
        inflater = (context as Activity).layoutInflater
    }

    constructor(context: Context, layoutResourceId: Int, stops: List<FavouriteStop>) : super(context, layoutResourceId, stops) {
        this.stops = stops
        this.layoutResourceId = layoutResourceId
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

        val favouriteStop: FavouriteStop

        if (stops != null)
            favouriteStop = stops!![position]
        else
            favouriteStop = FavouriteStopsList.get(position)

        holder.stopNumber!!.text = Integer.toString(favouriteStop.number)
        holder.stopName!!.text = favouriteStop.displayName

        return row
    }

    internal class StopHolder {
        var stopNumber: TextView? = null
        var stopName: TextView? = null
    }

    companion object {
        var sortPreference: FavouritesListSortType? = null
    }
}
