package com.kieran.winnipegbus.activities

import android.os.Bundle
import android.view.Menu

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.Stop

class StopsMapActivity : MapActivity() {
    private var cameraUpdate: CameraUpdate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_map)
        mapOnCreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_map, menu)
        return true
    }

    override fun onConnected(dataBundle: Bundle) {
        if (SearchResultsActivity.searchResults != null) {
            for (favouriteStop in SearchResultsActivity.searchResults.getStops()) {
                val markerOptions = MarkerOptions()
                markerOptions.position(favouriteStop.latLng)
                markerOptions.title(Integer.toString(favouriteStop.number))
                markerOptions.snippet(favouriteStop.name)
                map!!.addMarker(markerOptions)
            }
        }

        map!!.isTrafficEnabled = true
        map!!.setOnInfoWindowClickListener { marker -> openStopTimes(Stop(marker.snippet, Integer.parseInt(marker.title))) }
        onConnected()

        moveMap()
    }

    private fun moveMap() {
        var maxLat = 0.0
        var maxLng = 0.0
        var minLat = 0.0
        var minLng = 0.0

        if (cameraUpdate == null) {
            for (favouriteStop in SearchResultsActivity.searchResults.getStops()) {
                if (maxLat == 0.0 || favouriteStop.latLng!!.latitude > maxLat)
                    maxLat = favouriteStop.latLng!!.latitude
                if (maxLng == 0.0 || favouriteStop.latLng!!.longitude > maxLng)
                    maxLng = favouriteStop.latLng!!.longitude
                if (minLat == 0.0 || favouriteStop.latLng!!.latitude < minLat)
                    minLat = favouriteStop.latLng!!.latitude
                if (minLng == 0.0 || favouriteStop.latLng!!.longitude < minLng)
                    minLng = favouriteStop.latLng!!.longitude
            }

            if (maxLat != 0.0 && minLat != 0.0 && maxLng != 0.0 && minLng != 0.0) {
                val bounds = LatLngBounds(LatLng(minLat, minLng), LatLng(maxLat, maxLng))
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING)
            } else {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE), DEFAULT_ZOOM)
            }
            map!!.moveCamera(cameraUpdate!!)
        }
    }

    companion object {

        val DEFAULT_ZOOM = 11f
        val DEFAULT_LATITUDE = 49.8954
        val DEFAULT_LONGITUDE = -97.1385
        val MAP_PADDING = 50
    }
}
