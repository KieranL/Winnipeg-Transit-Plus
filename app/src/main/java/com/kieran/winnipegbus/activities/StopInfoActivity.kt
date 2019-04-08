package com.kieran.winnipegbus.activities

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.kieran.winnipegbus.adapters.StopFeaturesAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.common.StopFeatures
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.interfaces.TransitService
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import java.util.Locale

class StopInfoActivity : MapActivity() {
    private var stopFeatures: StopFeatures? = null
    private var adapter: StopFeaturesAdapter? = null
    private var task: Job? = null
    private lateinit var transitService: TransitService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_info)
        transitService = TransitServiceProvider.getTransitService()
        stopFeatures = intent.getSerializableExtra(STOP_FEATURES) as StopFeatures

        title = String.format(Locale.CANADA, ACTIONBAR_TEXT, stopFeatures!!.identifier)
        setTextViewText(R.id.stop_features_title, stopFeatures!!.name)

        val listView = findViewById<View>(R.id.listView_stop_features) as ListView
        adapter = StopFeaturesAdapter(this, R.layout.listview_stop_features_row, stopFeatures!!.getStopFeatures())
        listView.adapter = adapter

        task = GlobalScope.launch(IO) {
            try {
                stopFeatures = transitService.getStopDetails(stopFeatures!!.identifier, stopFeatures!!)
            } catch (e: Exception) {
                runOnUiThread { handleException(e) }
            }

            runOnUiThread { showStopFeatures() }
        }

        mapOnCreate()
    }

    override fun onStart() {
        super.onStart()
        connectClient()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (task != null)
            task!!.cancel()
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
        if (transitService.supportedFeatures().contains(SupportedFeature.STOP_FEATURES) && stopFeatures!!.numberOfFeatures() > 0) {
            runOnUiThread {
                adapter!!.notifyDataSetChanged()
            }
            val layout = findViewById<View>(R.id.stop_features_group) as RelativeLayout
            layout.visibility = View.VISIBLE
        }
    }

    companion object {
        val ACTIONBAR_TEXT = "Stop %d"
        val STOP_FEATURES = "stop_features"
    }
}
