package com.kieran.winnipegbus.Activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.ExpandableListView
import android.widget.Spinner
import android.widget.TimePicker

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.Adapters.TripPlannerAdapter
import com.kieran.winnipegbus.LocationPickerDialog
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.Views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.StopTime
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.TripPlanner.TimeMode
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip
import com.kieran.winnipegbusbackend.TripPlanner.classes.TripParameters

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.Calendar
import java.util.GregorianCalendar

class TripPlannerActivity : GoogleApiActivity(), TransitApiManager.OnJsonLoadResultReceiveListener, SwipeRefreshLayout.OnRefreshListener {
    private var tripParameters: TripParameters? = TripParameters()
    private var trips: MutableList<Trip>? = null
    private var adapter: TripPlannerAdapter? = null
    private var getDirectionsButton: Button? = null
    private var swipeRefresh: StyledSwipeRefresh? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trip_planner)
        val intent = intent

        tripParameters = intent.getSerializableExtra(PARAMETERS) as TripParameters

        if (tripParameters == null)
            tripParameters = TripParameters()

        trips = ArrayList()
        val listView = findViewById<View>(R.id.trip_planner_listview) as ExpandableListView
        listView.addHeaderView(layoutInflater.inflate(R.layout.listview_trip_planner_header, null))
        adapter = TripPlannerAdapter(this, trips)
        listView.setAdapter(adapter)

        getDirectionsButton = findViewById<View>(R.id.get_directions_button) as Button
        swipeRefresh = findViewById<View>(R.id.trip_planner_swipe_refresh) as StyledSwipeRefresh
        swipeRefresh!!.setOnRefreshListener(this)
        initializeFields()

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        connectClient()
    }

    private fun initializeFields() {
        val originButton = findViewById<View>(R.id.origin_select_button) as Button
        val destinationButton = findViewById<View>(R.id.destination_select_button) as Button

        if (tripParameters!!.origin != null)
            originButton.text = tripParameters!!.origin!!.title
        else
            originButton.text = "Select"

        if (tripParameters!!.destination != null)
            destinationButton.text = tripParameters!!.destination!!.title
        else
            destinationButton.text = "Select"

        setTextViewText(R.id.trip_time_hour_minute, tripParameters!!.time!!.toFormattedString(null, timeSetting))
        setTextViewText(R.id.trip_time_date, tripParameters!!.time!!.toDatePickerDateFormat())

        val timeModeView = findViewById<View>(R.id.time_mode_spinner) as Spinner
        val adapter = timeModeView.adapter

        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) as String == tripParameters!!.timeMode!!.name) {
                timeModeView.setSelection(i)
            }
        }

        timeModeView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                tripParameters!!.timeMode = TimeMode.getTimeModeByName(adapter.getItem(position) as String)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }

        getDirectionsButton!!.isEnabled = tripParameters!!.isValid
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_trip_planner, menu)
        return true
    }

    fun getDirections(view: View) {
        swipeRefresh!!.isRefreshing = true
        getDirections()
    }

    fun getDirections() {
        trips!!.clear()
        TransitApiManager.getJsonAsync(tripParameters!!.url, this)
    }

    fun pickTime(view: View) {
        val c = GregorianCalendar()
        val d = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            c.timeInMillis = tripParameters!!.time!!.milliseconds
            c.set(Calendar.HOUR_OF_DAY, hourOfDay)
            c.set(Calendar.MINUTE, minute)
            tripParameters!!.time = StopTime(c.timeInMillis)

            setTextViewText(R.id.trip_time_hour_minute, tripParameters!!.time!!.toFormattedString(null, timeSetting))
        }, tripParameters!!.time!!.hours, tripParameters!!.time!!.minutes, timeSetting)

        d.setTitle("")
        d.show()
    }

    fun pickDate(view: View) {
        val c = GregorianCalendar()
        val d = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            c.timeInMillis = tripParameters!!.time!!.milliseconds
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            tripParameters!!.time = StopTime(c.timeInMillis)

            setTextViewText(R.id.trip_time_date, tripParameters!!.time!!.toDatePickerDateFormat())
        }, tripParameters!!.time!!.year, tripParameters!!.time!!.month, tripParameters!!.time!!.dayOfMonth)

        d.setTitle("")
        d.show()
    }

    override fun OnReceive(result: LoadResult<JSONObject>) {
        val jsonObject = result.result
        try {
            val plans = jsonObject!!.getJSONArray("plans")

            for (i in 0 until plans.length()) {
                trips!!.add(Trip(plans.getJSONObject(i)))
            }

            adapter!!.notifyDataSetChanged()
            swipeRefresh!!.isRefreshing = false
        } catch (e: JSONException) {

        }

    }

    override fun onConnected(bundle: Bundle) {
        requestLocation()
    }

    fun selectOrigin(view: View) {
        LocationPickerDialog(this, LocationPickerDialog.OnLocationPickedListener { location ->
            tripParameters!!.origin = location
            initializeFields()
        }).show()
    }

    fun selectDestination(view: View) {
        LocationPickerDialog(this, LocationPickerDialog.OnLocationPickedListener { location ->
            tripParameters!!.destination = location
            initializeFields()
        }).show()
    }

    fun swapLocations(view: View) {
        tripParameters!!.swapLocations()
        initializeFields()
    }

    override fun onRefresh() {
        getDirections()
    }

    companion object {
        private val PARAMETERS = "parameters"
    }
}