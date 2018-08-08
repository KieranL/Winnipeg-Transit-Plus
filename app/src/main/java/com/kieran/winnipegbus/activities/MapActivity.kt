package com.kieran.winnipegbus.activities

import android.os.Bundle

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.kieran.winnipegbus.R

abstract class MapActivity : GoogleApiActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected var mapFragment: SupportMapFragment? = null
    protected var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun mapOnCreate() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        if (mapFragment != null) {
            mapFragment!!.getMapAsync { map -> loadMap(map) }
        }
    }

    protected fun loadMap(googleMap: GoogleMap) {
        map = googleMap
        if (map != null) {
            map!!.isMyLocationEnabled = true

            googleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build()

            connectClient()
        }
    }

    protected fun onConnected() {
        map!!.setOnMyLocationButtonClickListener {
            if (isLocationEnabled && isGooglePlayServicesAvailable) {
                false
            } else {
                showLongToaster("Location services are disabled")
                true
            }
        }
    }

    companion object {
        val DEFAULT_ZOOM = 11f
        val DEFAULT_LATITUDE = 49.8954
        val DEFAULT_LONGITUDE = -97.1385
        val MAP_PADDING = 50
    }
}
