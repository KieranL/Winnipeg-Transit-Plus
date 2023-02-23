package com.kieran.winnipegbus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.common.RecentStop

class RecentStopsAdapter(private val stops: List<RecentStop>, private val listener: ViewHolder.Listener):
        RecyclerView.Adapter<RecentStopsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View, listener: Listener) : RecyclerView.ViewHolder(view) {
        val stopId: TextView
        val stopName: TextView
        lateinit var stop: RecentStop

        init {
            // Define click listener for the ViewHolder's View.
            stopId = view.findViewById(R.id.recent_stop_id)
            stopName = view.findViewById(R.id.recent_stop_name)
            view.setOnClickListener {
                listener.onRecentStopSelected(stop)
            }
        }

        interface Listener {
            fun onRecentStopSelected(stop: RecentStop)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.listview_recent_stops_row, viewGroup, false)

        return ViewHolder(view, listener)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.stopId.text = stops[position].identifier.toString()
        viewHolder.stopName.text = stops[position].name
        viewHolder.stop = stops[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = stops.size

}