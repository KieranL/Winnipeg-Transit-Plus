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
import com.kieran.winnipegbusbackend.winnipegtransit.ServiceAdvisories.ServiceAdvisory

class ServiceAdvisoriesAdapter(context: Context, private val layoutResourceId: Int, private val advisories: List<ServiceAdvisory>) : ArrayAdapter<ServiceAdvisory>(context, layoutResourceId, advisories) {
    private val inflater: LayoutInflater
    private var use24hrTime: Boolean = false

    init {
        inflater = (context as Activity).layoutInflater
        loadTimeSetting()
    }

    fun loadTimeSetting() {
        use24hrTime = BaseActivity.getTimeSetting(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val holder: AdvisoryHolder

        if (row == null) {
            row = inflater.inflate(layoutResourceId, parent, false)

            holder = AdvisoryHolder()
            holder.title = row!!.findViewById<View>(R.id.service_advisory_title) as TextView
            holder.updatedTime = row.findViewById<View>(R.id.service_advisory_updated) as TextView

            row.tag = holder
        } else {
            holder = row.tag as AdvisoryHolder
        }

        val serviceAdvisory = advisories[position]

        holder.title!!.text = serviceAdvisory.title
        holder.updatedTime!!.text = serviceAdvisory.updatedAt.toFormattedDateString(use24hrTime)

        return row
    }

    private class AdvisoryHolder {
        internal var title: TextView? = null
        internal var updatedTime: TextView? = null
    }
}
