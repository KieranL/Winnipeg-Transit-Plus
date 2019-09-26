package com.kieran.winnipegbus.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.ShakeDetector
import com.kieran.winnipegbus.adapters.StopTimeAdapter
import com.kieran.winnipegbus.views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes.StopLocation
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes.TripParameters
import com.kieran.winnipegbusbackend.common.*
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.rollbar.android.Rollbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*

class StopTimesActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ShakeDetector.OnShakeListener {
    private var stopSchedule: StopSchedule? = null
    private var stopName: String? = null
    private var loading = false
    private val stops = ArrayList<ScheduledStop>()
    private lateinit var adapter: StopTimeAdapter
    private val routeNumberFilter = ArrayList<RouteIdentifier>()
    private lateinit var title: TextView
    private var selectedRoutes: BooleanArray? = null
    private val routeFilterRoutes = ArrayList<Route>()
    private var loadStopTimesTask: Job? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var shakeDetector: ShakeDetector? = null
    private var hasFilterChanged: Boolean = false
    private var swipeRefreshLayout: StyledSwipeRefresh? = null
    private lateinit var lastUpdatedView: TextView
    private var lastUpdated: StopTime? = null
    private var favouriteButton: MenuItem? = null
    private lateinit var favouriteStop: FavouriteStop

    private val scheduleEndTime: StopTime
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val endTime = StopTime()
            endTime.increaseHour(Integer.parseInt(prefs.getString(getString(R.string.pref_schedule_load_interval), "2")!!))

            return endTime
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_times)

        adViewResId = R.id.stopTimesAdView

        val listView = findViewById<ListView>(R.id.stop_times_listview)!!

        swipeRefreshLayout = findViewById(R.id.stop_times_swipe_refresh)
        swipeRefreshLayout!!.setOnRefreshListener(this)


        listView.onItemClickListener = this

        adapter = StopTimeAdapter(this, R.layout.listview_stop_times_row, stops)
        listView.addHeaderView(layoutInflater.inflate(R.layout.listview_stop_times_header, null))
        listView.adapter = adapter

        val intent = intent

        title = findViewById(R.id.listView_stop_times_header_text)
        lastUpdatedView = findViewById(R.id.stop_times_header_last_updated)

        val stopExtra = intent.getSerializableExtra(STOP)

        val stop = stopExtra as FavouriteStop
        favouriteStop = stop

        if (favouriteStop.id < 0) {
            val existingFavourites = favouritesService.getAll()

            val existingFavourite = existingFavourites.find {
                it.identifier == favouriteStop.identifier && it.routes == null
            }

            if (existingFavourite != null)
                favouriteStop = existingFavourite
        }

        if (stop.routes != null) {
            routeNumberFilter.addAll(stop.routes!!)
            selectedRoutes = BooleanArray(routeNumberFilter.size) { true }
        }

        stopName = stop.name
        title.text = stopName

        setTitle(String.format(Locale.CANADA, ACTIONBAR_TEXT, stop.identifier.toString()))

        initializeAdsIfEnabled()
        createShakeListener()
        loading = false
    }

    private fun createShakeListener() {
        val packageManager = packageManager

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            shakeDetector = ShakeDetector(this)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        loading = false
        if (loadStopTimesTask != null)
            loadStopTimesTask!!.cancel()
    }

    public override fun onResume() {
        super.onResume()

        if (sensorManager != null)
            sensorManager!!.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)

        if (isBooleanSettingEnabled("pref_refresh_on_resume", true)) {
            if (lastUpdated != null && System.currentTimeMillis() - StaticConfig.STOP_TIMES_MIN_AUTO_REFRESH_MILLISECONDS_DELTA > lastUpdated!!.milliseconds)
                refresh()
        }
    }

    public override fun onPause() {
        super.onPause()
        loading = false

        if (sensorManager != null)
            sensorManager!!.unregisterListener(shakeDetector)
    }

    private fun openAdditionalInfo(position: Int) {
        val intent: Intent
        if (stopSchedule != null) {
            if (position == 0) {
                intent = Intent(this, StopInfoActivity::class.java)
                intent.putExtra(StopInfoActivity.STOP_FEATURES, stopSchedule!!.createStopFeatures())
            } else {
                intent = Intent(this, ScheduledStopInfoActivity::class.java)
                intent.putExtra(ScheduledStopInfoActivity.STOP_EXTRA, stops[position - 1])
            }
            startActivity(intent)
        }
    }

    private fun getTimes() {
        loadStopTimesTask = GlobalScope.launch(Dispatchers.IO) {
            try {
                val stopSchedule = transitService.getStopSchedule(favouriteStop.identifier, null, scheduleEndTime, routeNumberFilter)

                runOnUiThread {
                    if (loading) {
                        stops.clear()
                        stops.addAll(stopSchedule.scheduledStopsSorted)
                    }
                    adapter.notifyDataSetChanged()
                    stopName = stopSchedule.name
                    title.text = stopName
                    this@StopTimesActivity.stopSchedule = stopSchedule
                }
            } catch (ex: Exception) {
                Rollbar.instance()?.error(ex)
                runOnUiThread {
                    handleException(ex)

                    if (loading) {
                        if (stopSchedule == null && ex is IOException)
                            title.setText(R.string.network_error)
                    }
                }
            }

            runOnUiThread {
                lastUpdated = StopTime()
                lastUpdatedView.text = String.format(UPDATED_STRING, lastUpdated!!.toFormattedString(null, timeSetting))
                swipeRefreshLayout!!.isRefreshing = false
                favouriteButton?.icon = getFavouritesButtonDrawable()
                loading = false
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stop_times, menu)

        val features = transitService.supportedFeatures()

        if (features.contains(SupportedFeature.TRIP_PLANNING)) {
            menu.findItem(R.id.trip_planner).isVisible = true
        }

        favouriteButton = menu.findItem(R.id.add_to_favourites_button)
        favouriteButton?.icon = getFavouritesButtonDrawable()
        onOptionsItemSelected(menu.findItem(R.id.action_refresh)) //manually click the refresh button, this is the only way the swipe refresh loading spinner works correctly on initial load. Not happy with this but it was the only way I could get it to work
        return true
    }

    private fun getFavouritesButtonDrawable(): Drawable {
        val themeId = themeResId
        val drawableId: Int
        val filterMatches = doesFilterMatchRoutes()

        drawableId = if (favouriteStop.id > 0)
            if (filterMatches && favouriteStop.routes != null)
                if (themeId == R.style.Light) R.drawable.ic_filtered_favourite_light else R.drawable.ic_filtered_favourite_dark
            else if (favouriteStop.routes == null && !routeNumberFilter.any())
                if (themeId == R.style.Light) R.drawable.ic_favourite_stops_light else R.drawable.ic_favourite_stops_dark
            else
                if (themeId == R.style.Light) R.drawable.ic_add_to_favourites_light else R.drawable.ic_add_to_favourites_dark
        else
            if (themeId == R.style.Light) R.drawable.ic_add_to_favourites_light else R.drawable.ic_add_to_favourites_dark

        return resources.getDrawable(drawableId)
    }

    private fun doesFilterMatchRoutes(): Boolean {
        if (stopSchedule == null || favouriteStop.routes == null)
            return false

        if (favouriteStop.routes!!.count() != stopSchedule!!.getRouteList().count())
            return false

        for (routeIdentifier in favouriteStop.routes!!) {
            var found = false

            for (route in stopSchedule!!.getRouteList()) {
                if (route.routeIdentifier == routeIdentifier) {
                    found = true
                    break
                }
            }

            if (!found) {
                return false
            }
        }

        return true
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true

                adapter.loadTimeSetting()

                getTimes()
            } else {
                showLongToaster(R.string.network_error)
            }
        }
        swipeRefreshLayout!!.isRefreshing = loading
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                refresh()
                return true
            }
            R.id.add_to_favourites_button -> {
                handleFavouritesClick(item)
                return true
            }
            R.id.action_filter -> {
                if (!loading && stopSchedule != null)
                    openFilterWindow()
                else
                    showLongToaster(R.string.wait_for_load)
                return true
            }
            R.id.get_directions_button -> {
                if (!loading) {
                    val intent = Intent(this, TripPlannerActivity::class.java)
                    val parameters = TripParameters()

                    parameters.origin = StopLocation(stopSchedule)
                    intent.putExtra(TripPlannerActivity.PARAMETERS, parameters)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleFavouritesClick(item: MenuItem) {
        if (favouriteStop.id > 0 && ((favouriteStop.routes == null && !routeNumberFilter.any()) || doesFilterMatchRoutes())) {
            openDeleteFavouriteDialog(item)
        } else if (stopName != null && stopName != "") {
            val newFavouriteStop = favouritesService.add(FavouriteStop(stopName!!, favouriteStop.identifier, routes = routeNumberFilter))

            if (newFavouriteStop != null) {
                favouriteStop = newFavouriteStop
                item.icon = getFavouritesButtonDrawable()
            } else {
                showShortToaster(R.string.unknown_error)
            }
        } else {
            showLongToaster(R.string.wait_for_load)
        }
    }

    private fun openDeleteFavouriteDialog(item: MenuItem) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(DELETE_THIS_FAVOURITE)

        alertDialog.setPositiveButton(R.string.yes) { _, _ ->
            favouritesService.delete(favouriteStop.id)
            favouriteStop = FavouriteStop(favouriteStop.name, favouriteStop.identifier)
            item.icon = getFavouritesButtonDrawable()
        }

        alertDialog.setNegativeButton(R.string.no, null)
        alertDialog.create().show()
    }

    private fun openFilterWindow() {
        if (stopSchedule != null) {
            hasFilterChanged = false
            val filterDialog = AlertDialog.Builder(this)
            filterDialog.setTitle(R.string.filter_dialog_title)

            if (routeFilterRoutes.size == 0)
                getFilterRoutes()

            if (routeFilterRoutes.size < stopSchedule!!.getRouteList().size) {
                routeFilterRoutes.clear()
                getFilterRoutes()
            }

            val charSequence = arrayOfNulls<CharSequence>(routeFilterRoutes.size)

            if (selectedRoutes == null || (selectedRoutes!!.size < stopSchedule!!.getRouteList().size))
                selectedRoutes = BooleanArray(routeFilterRoutes.size)


            for (i in charSequence.indices)
                charSequence[i] = routeFilterRoutes[i].toString()

            filterDialog.setMultiChoiceItems(charSequence, selectedRoutes) { _, which, isChecked ->
                hasFilterChanged = true
                val routeNumber = routeFilterRoutes[which].routeIdentifier
                selectedRoutes!![which] = isChecked
                if (isChecked)
                    routeNumberFilter.add(routeNumber)
                else
                    routeNumberFilter.remove(routeNumber)
            }

            filterDialog.setPositiveButton(FILTER_POSITIVE) { _, _ ->
                if (hasFilterChanged) {
                    refresh()
                }
            }

            filterDialog.create().show()
        } else {
            showLongToaster(R.string.wait_for_load)
        }
    }

    private fun getFilterRoutes() {
        for (routeSchedule in stopSchedule!!.getRouteList())
            routeFilterRoutes.add(Route(routeSchedule))

        routeFilterRoutes.sort()
    }

    override fun onRefresh() {
        refresh()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openAdditionalInfo(position)
    }

    override fun onShake() {
        if (isBooleanSettingEnabled("pref_refresh_on_shake")) {
            refresh()
        }
    }

    companion object {
        private const val FILTER_POSITIVE = "Done"
        const val STOP = "stop"
        private const val UPDATED_STRING = "Updated %s"
        private const val ACTIONBAR_TEXT = "Stop %s"
        private const val DELETE_THIS_FAVOURITE = "Delete this Favourite?"
    }
}