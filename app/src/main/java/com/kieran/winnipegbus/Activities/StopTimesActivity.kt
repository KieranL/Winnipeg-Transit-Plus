package com.kieran.winnipegbus.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView

import com.kieran.winnipegbus.Adapters.StopTimeAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.ShakeDetector
import com.kieran.winnipegbus.Views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.FavouriteStopsList
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.Route
import com.kieran.winnipegbusbackend.RouteSchedule
import com.kieran.winnipegbusbackend.ScheduledStop
import com.kieran.winnipegbusbackend.Stop
import com.kieran.winnipegbusbackend.StopSchedule
import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.TransitApiManager

import org.json.JSONObject

import java.io.IOException
import java.util.ArrayList
import java.util.Collections
import java.util.Locale

class StopTimesActivity : BaseActivity(), SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ShakeDetector.OnShakeListener {
    private var stopSchedule: StopSchedule? = null

    private var stopNumber: Int = 0
    private var stopName: String? = null
    private var loading = false
    private val stops = ArrayList<ScheduledStop>()
    private var adapter: StopTimeAdapter? = null
    private val routeNumberFilter = ArrayList<Int>()
    private var title: TextView? = null
    private var selectedRoutes: BooleanArray? = null
    private val routeFilterRoutes = ArrayList<Route>()
    private var loadStopTimesTask: AsyncTask<*, *, *>? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var shakeDetector: ShakeDetector? = null
    private var hasFilterChanged: Boolean = false
    private var swipeRefreshLayout: StyledSwipeRefresh? = null
    private var lastUpdated: TextView? = null

    private val scheduleEndTime: StopTime
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val endTime = StopTime()
            endTime.increaseHour(Integer.parseInt(prefs.getString(getString(R.string.pref_schedule_load_interval), "2")))

            return endTime
        }

    override fun onNewIntent(intent: Intent) {
        val newStopNumber = (intent.getSerializableExtra(STOP) as Stop).number
        if (newStopNumber != stopNumber) {
            stopNumber = newStopNumber
            setIntent(intent)
            recreate()
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_times)
        adViewResId = R.id.stopTimesAdView

        FavouriteStopsList.loadFavourites()

        val listView = findViewById<ListView>(R.id.stop_times_listview)

        swipeRefreshLayout = findViewById(R.id.stop_times_swipe_refresh)
        swipeRefreshLayout!!.setOnRefreshListener(this)


        listView.onItemClickListener = this

        adapter = StopTimeAdapter(this, R.layout.listview_stop_times_row, stops)
        listView.addHeaderView(layoutInflater.inflate(R.layout.listview_stop_times_header, null))
        listView.adapter = adapter

        val intent = intent

        if (title == null) {
            title = findViewById(R.id.listView_stop_times_header_text)
            lastUpdated = findViewById(R.id.stop_times_header_last_updated)
        }

        val stop = intent.getSerializableExtra(STOP) as Stop
        stopName = stop.name
        title!!.text = stopName
        stopNumber = stop.number

        setTitle(String.format(Locale.CANADA, ACTIONBAR_TEXT, stopNumber))

        initializeAdsIfEnabled()
        createShakeListener()
        loading = false
    }

    fun createShakeListener() {
        val PM = packageManager

        if (PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            shakeDetector = ShakeDetector(this)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        loading = false
        if (loadStopTimesTask != null)
            loadStopTimesTask!!.cancel(true)
    }

    public override fun onResume() {
        super.onResume()

        if (sensorManager != null)
            sensorManager!!.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI)
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
        val urlPath = TransitApiManager.generateStopNumberURL(stopNumber, routeNumberFilter, null, scheduleEndTime)

        loadStopTimesTask = LoadStopTimes().execute(urlPath)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stop_times, menu)

        menu.findItem(R.id.add_to_favourites_button).icon = getFavouritesButtonDrawable(FavouriteStopsList.contains(stopNumber))
        onOptionsItemSelected(menu.findItem(R.id.action_refresh)) //manually click the refresh button, this is the only way the swipe refresh loading spinner works correctly on initial load. Not happy with this but it was the only way I could get it to work
        return true
    }

    private fun getFavouritesButtonDrawable(isFavoured: Boolean): Drawable {
        val themeId = themeResId
        val drawableId: Int

        if (isFavoured)
            drawableId = if (themeId == R.style.Light) R.drawable.ic_favourite_stops_light else R.drawable.ic_favourite_stops_dark
        else
            drawableId = if (themeId == R.style.Light) R.drawable.ic_add_to_favourites_light else R.drawable.ic_add_to_favourites_dark

        return resources.getDrawable(drawableId)
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true

                adapter!!.loadTimeSetting()

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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleFavouritesClick(item: MenuItem) {
        if (FavouriteStopsList.contains(stopNumber)) {
            openDeleteFavouriteDialog(item)
        } else if (stopName != null && stopName != "") {
            FavouriteStopsList.addToFavourites(FavouriteStop(stopName, stopNumber))
            item.icon = getFavouritesButtonDrawable(true)
        } else {
            showLongToaster(R.string.wait_for_load)
        }
    }

    private fun openDeleteFavouriteDialog(item: MenuItem) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage(DELETE_THIS_FAVOURITE)

        alertDialog.setPositiveButton(DIALOG_YES) { dialogInterface, which ->
            FavouriteStopsList.remove(stopNumber)
            item.icon = getFavouritesButtonDrawable(false)
        }

        alertDialog.setNegativeButton(DIALOG_NO, null)
        alertDialog.create().show()
    }

    private fun openFilterWindow() {
        if (stopSchedule != null) {
            hasFilterChanged = false
            val filterDialog = AlertDialog.Builder(this)
            filterDialog.setTitle(R.string.filter_dialog_title)

            if (routeFilterRoutes.size == 0)
                getFilterRoutes()

            if (routeFilterRoutes.size < stopSchedule!!.routeList.size) {
                routeFilterRoutes.clear()
                getFilterRoutes()
            }

            val charSequence = arrayOfNulls<CharSequence>(routeFilterRoutes.size)

            if (selectedRoutes == null)
                selectedRoutes = BooleanArray(routeFilterRoutes.size)

            for (i in charSequence.indices)
                charSequence[i] = routeFilterRoutes[i].toString()

            filterDialog.setMultiChoiceItems(charSequence, selectedRoutes, object : AlertDialog.OnMultiChoiceClickListener {
                override fun onClick(dialog: DialogInterface, which: Int, isChecked: Boolean) {
                    hasFilterChanged = true
                    val routeNumber = routeFilterRoutes[which].routeNumber
                    selectedRoutes[which] = isChecked
                    if (isChecked)
                        routeNumberFilter.add(routeNumber)
                    else
                        routeNumberFilter.remove(Integer.valueOf(routeNumber))
                }
            })

            filterDialog.setPositiveButton(FILTER_POSITIVE) { dialog, which ->
                if (hasFilterChanged) {
                    refresh()
                }
            }

            filterDialog.create().show()
        } else {
            showLongToaster(R.string.wait_for_load)
        }
    }

    fun getFilterRoutes() {
        for (routeSchedule in stopSchedule!!.routeList)
            routeFilterRoutes.add(Route(routeSchedule))

        Collections.sort(routeFilterRoutes)
    }

    override fun onRefresh() {
        refresh()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openAdditionalInfo(position)
    }

    override fun onShake() {
        refresh()
    }

    private inner class LoadStopTimes : AsyncTask<String, Void, LoadResult<JSONObject>>() {
        override fun doInBackground(vararg urls: String): LoadResult<JSONObject> {
            val result: LoadResult<JSONObject>

            result = TransitApiManager.getJson(urls[0])

            if (loading && result.result != null) {
                if (stopSchedule == null) {
                    stopSchedule = StopSchedule(result.result, stopNumber)

                    stopName = stopSchedule!!.name
                } else {
                    stopSchedule!!.refresh(result.result)
                }
            }

            return result
        }

        protected override fun onPostExecute(result: LoadResult<*>) {
            if (loading && result.result != null) {
                stops.clear()
                stops.addAll(stopSchedule!!.scheduledStopsSorted)
            }

            if (result.exception != null && loading) {
                handleException(result.exception)
                if (stopSchedule == null && result.exception is IOException)
                    title!!.setText(R.string.network_error)
            } else if (stops.size == 0 && loading) {
                showLongToaster(R.string.no_results_found)
                title!!.setText(R.string.no_results_found)
            } else {
                title!!.text = stopName
            }

            adapter!!.notifyDataSetChanged()

            lastUpdated!!.text = String.format(UPDATED_STRING, StopTime().toFormattedString(null, timeSetting))
            swipeRefreshLayout!!.isRefreshing = false
            loading = false
        }
    }

    companion object {

        private val FILTER_POSITIVE = "Done"
        val STOP = "stop"
        private val UPDATED_STRING = "Updated %s"
        private val ACTIONBAR_TEXT = "Stop %d"
        private val DELETE_THIS_FAVOURITE = "Delete this Favourite?"
        private val DIALOG_YES = "Yes"
        private val DIALOG_NO = "No"
        private val CREATE_NOTIFICATION_FOR_BUS = "Create a notification for this bus?"
    }
}