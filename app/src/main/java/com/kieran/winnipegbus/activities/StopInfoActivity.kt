package com.kieran.winnipegbus.activities

import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.kieran.winnipegbus.adapters.StopFeaturesAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.StopFeatures
import com.kieran.winnipegbusbackend.TransitApiManager

import org.json.JSONObject

import java.util.Locale

class StopInfoActivity : MapActivity(), TransitApiManager.OnJsonLoadResultReceiveListener {
    private var stopFeatures: StopFeatures? = null
    private var adapter: StopFeaturesAdapter? = null
    private var task: AsyncTask<*, *, *>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_info)
        stopFeatures = intent.getSerializableExtra(STOP_FEATURES) as StopFeatures

        title = String.format(Locale.CANADA, ACTIONBAR_TEXT, stopFeatures!!.number)
        setTextViewText(R.id.stop_features_title, stopFeatures!!.name)

        val listView = findViewById<View>(R.id.listView_stop_features) as ListView
        adapter = StopFeaturesAdapter(this, R.layout.listview_stop_features_row, stopFeatures!!.getStopFeatures())
        listView.adapter = adapter

        task = TransitApiManager.getJsonAsync(TransitApiManager.generateStopFeaturesUrl(stopFeatures!!.number), this)
        mapOnCreate()
    }

    override fun onStart() {
        super.onStart()
        connectClient()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (task != null)
            task!!.cancel(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_stop_info, menu)
        return true
    }

    override fun onConnected(dataBundle: Bundle?) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(stopFeatures!!.getLatLng(), 17f)
        map!!.moveCamera(cameraUpdate)
        val markerOptions = MarkerOptions()
        markerOptions.position(stopFeatures!!.getLatLng())
        map!!.addMarker(markerOptions)
        map!!.isTrafficEnabled = true

        onConnected()
    }

    private fun showStopFeatures() {
        if (stopFeatures!!.numberOfFeatures() > 0) {
            adapter!!.notifyDataSetChanged()
            val layout = findViewById<View>(R.id.stop_features_group) as RelativeLayout
            layout.visibility = View.VISIBLE
        }
    }

    override fun onReceive(result: LoadResult<JSONObject>) {
        if (result.result != null) {
            stopFeatures!!.loadFeatures(result.result)
            showStopFeatures()
        } else if (result.exception != null) {
            handleException(result.exception)
        }
    }

    companion object {
        val ACTIONBAR_TEXT = "Stop %d"
        val STOP_FEATURES = "stop_features"
    }
}
