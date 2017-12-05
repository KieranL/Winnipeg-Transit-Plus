package com.kieran.winnipegbus.Adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.StopFeature

class StopFeaturesAdapter(context: Context, private val layoutResourceId: Int, private val stopFeatures: List<StopFeature>) : ArrayAdapter<StopFeature>(context, layoutResourceId, stopFeatures) {
    private val inflater: LayoutInflater

    init {
        inflater = (context as Activity).layoutInflater
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }

    override fun getView(position: Int, row: View?, parent: ViewGroup): View {
        var row = row
        val holder: StopHolder

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false)

            holder = StopHolder()
            holder.name = row!!.findViewById<View>(R.id.stop_feature_name) as TextView
            holder.count = row.findViewById<View>(R.id.stop_feature_count) as TextView
            row.tag = holder
        } else {
            holder = row.tag as StopHolder
        }

        val stopFeature = stopFeatures[position]
        holder.name!!.text = stopFeature.name
        holder.count!!.text = Integer.toString(stopFeature.count)
        return row
    }

    private class StopHolder {
        internal var name: TextView? = null
        internal var count: TextView? = null
    }
}
