package com.kieran.winnipegbus.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.adapters.UpcomingStopsAdapter
import com.kieran.winnipegbus.views.RouteNumberTextView
import com.kieran.winnipegbus.views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.ScheduledStop
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.UpcomingStop
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.interfaces.TransitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


import java.util.*
import kotlin.collections.ArrayList

class ScheduledStopInfoActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener {
    private var upcomingStops: ArrayList<UpcomingStop> = ArrayList()
    private var scheduledStop: ScheduledStop? = null
    private var use24hrTime: Boolean = false
    private var adapter: UpcomingStopsAdapter? = null
    private var task: Job? = null
    private var loading = false
    private var swipeRefreshLayout: StyledSwipeRefresh? = null
    private lateinit var transitService: TransitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scheduled_stop_info)
        scheduledStop = intent.getSerializableExtra(STOP_EXTRA) as ScheduledStop?
        transitService = TransitServiceProvider.getTransitService()

        if (scheduledStop != null) {
            use24hrTime = timeSetting

            val listView = findViewById<View>(R.id.listView_upcoming_stops) as ListView
            val headerView = layoutInflater.inflate(R.layout.listview_upcoming_stops_header, null)
            headerView.isEnabled = false
            headerView.isClickable = false
            listView.addHeaderView(headerView)

            adapter = UpcomingStopsAdapter(this, R.layout.upcoming_stops_row, upcomingStops, use24hrTime)
            listView.adapter = adapter

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

        if (task != null)
            task?.cancel()
    }

    @SuppressLint("SetTextI18n")
    private fun fillTextViews() {
        val scheduledStop = this.scheduledStop
        if (scheduledStop != null) {
            val routeNumber = getTextView(R.id.bus_number) as RouteNumberTextView
            routeNumber.text = Integer.toString(scheduledStop.routeNumber)
            routeNumber.setColour(scheduledStop.routeNumber, scheduledStop.coverageType)

            setTextViewText(R.id.bus_name, scheduledStop.routeVariantName!!)

            setTextViewText(R.id.scheduled_departure, scheduledStop.scheduledDepartureTime!!.toFormattedString(null, use24hrTime))
            setTextViewText(R.id.estimated_departure, scheduledStop.estimatedDepartureTime!!.toFormattedString(null, use24hrTime))

            setTextViewText(R.id.has_bike_rack, String.format(BIKE_RACK, booleanStringValue(scheduledStop.hasBikeRack)))
            setTextViewText(R.id.has_wifi, String.format(WIFI, booleanStringValue(scheduledStop.hasWifi)))

            var busNumberText = String.format(BUS_NUMBER, "Unknown")

            if (scheduledStop.busNumber != 0) {
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
                if(transitService.supportedFeatures().contains(SupportedFeature.UPCOMING_STOPS)) {
                    loading = true

                    task = GlobalScope.launch(Dispatchers.IO) {
                        try {
                            val routeKey = scheduledStop?.routeKey
                            val key = scheduledStop?.key
                            val estimatedDepartureTime = scheduledStop?.estimatedDepartureTime

                            if(routeKey != null && key != null && estimatedDepartureTime != null) {
                                val stops = transitService.getUpcomingStops(routeKey, key, estimatedDepartureTime)

                                runOnUiThread {
                                    upcomingStops.clear()
                                    upcomingStops.addAll(stops)
                                    Collections.sort(upcomingStops)
                                    adapter?.notifyDataSetChanged()
                                }
                            }else {
                                runOnUiThread { showShortToaster(R.string.unknown_error) }
                            }
                        }catch (e: Exception) {
                            runOnUiThread {
                            handleException(e)}
                        }
                        runOnUiThread {
                            swipeRefreshLayout?.isRefreshing = false
                        }
                        loading = false
                    }
                }
            } else {
                runOnUiThread {
                    showLongToaster(R.string.network_error)
                }
            }
        }
        runOnUiThread {
            swipeRefreshLayout?.isRefreshing = loading
        }
        fillTextViews()
    }

    override fun onRefresh() {
        refresh()
    }

    companion object {
        val BIKE_RACK = "Bike rack: %s"
        val WIFI = "Wi-Fi: %s"
        val BUS_NUMBER = "Bus Number: %s"
        val STOP_EXTRA = "stop"
    }
}