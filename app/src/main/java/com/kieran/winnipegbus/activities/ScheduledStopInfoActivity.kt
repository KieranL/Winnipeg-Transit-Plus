package com.kieran.winnipegbus.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView

import com.kieran.winnipegbus.adapters.UpcomingStopsAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.views.RouteNumberTextView
import com.kieran.winnipegbus.views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.ScheduledStop
import com.kieran.winnipegbusbackend.StopSchedule
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.UpcomingStop
import com.kieran.winnipegbusbackend.UpcomingStops.HttpUpcomingStopsManager
import com.kieran.winnipegbusbackend.UpcomingStops.UpcomingStopsManager

import org.json.JSONObject

import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Collections

class ScheduledStopInfoActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, TransitApiManager.OnJsonLoadResultReceiveListener, UpcomingStopsManager.OnUpcomingStopsFoundListener {
    private var upcomingStops: ArrayList<UpcomingStop>? = null
    private var scheduledStop: ScheduledStop? = null
    private var use24hrTime: Boolean = false
    private var adapter: UpcomingStopsAdapter? = null
    private var tasks: ArrayList<AsyncTask<*, *, *>>? = null
    private var loading = false
    private var swipeRefreshLayout: StyledSwipeRefresh? = null
    private var upcomingStopsManager: UpcomingStopsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_stop_info)
        scheduledStop = intent.getSerializableExtra(STOP_EXTRA) as ScheduledStop
        upcomingStopsManager = HttpUpcomingStopsManager()

        if (scheduledStop != null) {
            use24hrTime = timeSetting
            upcomingStops = ArrayList()

            val listView = findViewById<View>(R.id.listView_upcoming_stops) as ListView
            val headerView = layoutInflater.inflate(R.layout.listview_upcoming_stops_header, null)
            headerView.isEnabled = false
            headerView.isClickable = false
            listView.addHeaderView(headerView)

            adapter = UpcomingStopsAdapter(this, R.layout.upcoming_stops_row, upcomingStops!!, use24hrTime)
            listView.adapter = adapter
            tasks = ArrayList()

            swipeRefreshLayout = findViewById<View>(R.id.upcoming_stops_swipeRefresh) as StyledSwipeRefresh
            swipeRefreshLayout!!.setOnRefreshListener(this)

            fillTextViews()
        } else {
            finish()
        }
    }

    public override fun onResume() {
        super.onResume()
        use24hrTime = timeSetting
        fillTextViews()
    }

    public override fun onDestroy() {
        super.onDestroy()

        if (tasks != null)
            for (task in tasks!!)
                task.cancel(true)
    }

    @SuppressLint("SetTextI18n")
    private fun fillTextViews() {
        val scheduledStop = this.scheduledStop
        if (scheduledStop != null) {
            val routeNumber = getTextView(R.id.bus_number) as RouteNumberTextView
            routeNumber.text = Integer.toString(scheduledStop.routeNumber)
            routeNumber.setColour(scheduledStop)

            setTextViewText(R.id.bus_name, scheduledStop.routeVariantName!!)

            setTextViewText(R.id.scheduled_departure, scheduledStop.scheduledDepartureTime!!.toFormattedString(null, use24hrTime))
            setTextViewText(R.id.estimated_departure, scheduledStop.estimatedDepartureTime!!.toFormattedString(null, use24hrTime))

            setTextViewText(R.id.has_bike_rack, String.format(BIKE_RACK, booleanStringValue(scheduledStop.hasBikeRack)))
            setTextViewText(R.id.has_wifi, String.format(WIFI, booleanStringValue(scheduledStop.hasWifi)))

            var busNumberText = String.format(BUS_NUMBER, "Unknown")

            if(scheduledStop.busNumber != 0) {
                busNumberText = String.format(BUS_NUMBER, scheduledStop.busNumber.toString())
            }

            setTextViewText(R.id.details_bus_number, busNumberText)
        }
    }

    private fun booleanStringValue(b: Boolean): String {
        return if (b) "Yes" else "No"
    }

    private fun getTextView(id: Int): TextView {
        return findViewById<View>(id) as TextView
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scheduled_stop_info, menu)

        onOptionsItemSelected(menu.findItem(R.id.action_refresh))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true
                upcomingStops!!.clear()

                upcomingStopsManager!!.GetUpcomingStopsAsync(scheduledStop!!.routeKey!!, scheduledStop!!.key!!.stopNumber, this)
            } else {
                showLongToaster(R.string.network_error)
            }
        }
        swipeRefreshLayout!!.isRefreshing = loading
        fillTextViews()
    }

    override fun onRefresh() {
        refresh()
    }

    override fun onReceive(result: LoadResult<JSONObject>) {
        if (result.result != null) {
            val stopSchedule = StopSchedule(result.result)
            val scheduledStop1 = stopSchedule.getScheduledStopByKey(scheduledStop!!.key!!)

            if (scheduledStop1 != null) {
                if (scheduledStop1.key!!.stopNumber == scheduledStop!!.key!!.stopNumber) {
                    scheduledStop = scheduledStop1
                }

                val upcomingStop = UpcomingStop(stopSchedule, scheduledStop1.estimatedDepartureTime!!, scheduledStop1.key!!)
                upcomingStops!!.add(upcomingStop)
            }
        } else if (result.exception != null) {
            handleException(result.exception)

            if (result.exception is FileNotFoundException) {
                for (task in tasks!!)
                    task.cancel(true)

                tasks!!.clear()
            }
        }

        if (tasks!!.size <= 2) {
            Collections.sort(upcomingStops!!)
            adapter!!.notifyDataSetChanged()
            swipeRefreshLayout!!.isRefreshing = false
            loading = false
        }

        removeFinishedTasks()
    }

    private fun removeFinishedTasks() {
        val finishedTasks = tasks!!.filterTo(ArrayList()) { it.status == AsyncTask.Status.FINISHED }

        tasks!!.removeAll(finishedTasks)
    }

    override fun OnUpcomingStopsFound(result: LoadResult<ArrayList<Int>>) {
        val instance = this
        if (result.result != null) {
            if (result.result.size > 0) {
                for (stopNumber in result.result) {

                    try {
                        val latest = if (scheduledStop!!.estimatedDepartureTime!!.milliseconds > TransitApiManager.lastQueryTime!!.milliseconds) scheduledStop!!.estimatedDepartureTime else TransitApiManager.lastQueryTime

                        val task = TransitApiManager.getJsonAsync(TransitApiManager.generateStopNumberURL(stopNumber, scheduledStop!!.routeNumber, latest!!, null), instance)
                        tasks!!.add(task)
                    } catch (e: Exception) {
                        Log.e("Task", "task error")
                    }

                }
            }


        } else if (result.exception != null) {
            handleException(result.exception)

            Collections.sort(upcomingStops!!)
            adapter!!.notifyDataSetChanged()
            swipeRefreshLayout!!.isRefreshing = false
            loading = false
            tasks!!.clear()
        }
    }

    companion object {
        val BIKE_RACK = "Bike rack: %s"
        val WIFI = "Wi-Fi: %s"
        val BUS_NUMBER = "Bus Number: %s"
        val STOP_EXTRA = "stop"
    }
}