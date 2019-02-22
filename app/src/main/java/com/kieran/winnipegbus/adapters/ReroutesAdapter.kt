package com.kieran.winnipegbus.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

import com.kieran.winnipegbusbackend.winnipegtransit.ServiceAdvisories.Reroute

class ReroutesAdapter(private val context: Context, private val reroutes: List<Reroute>) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int {
        return reroutes.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return reroutes[groupPosition].instructions.size
    }

    override fun getGroup(groupPosition: Int): Any {
        return reroutes[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return reroutes[groupPosition].instructions[childPosition]
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

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View, parent: ViewGroup): View {
        val textView = TextView(context)
        val s = (getGroup(groupPosition) as Reroute).heading
        textView.text = s
        return textView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, b: Boolean, view: View, viewGroup: ViewGroup): View {
        val textView = TextView(context)
        val s = (getGroup(groupPosition) as Reroute).instructions[childPosition]
        textView.text = s
        return textView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return (getGroup(groupPosition) as Reroute).instructions.isNotEmpty()
    }
}
