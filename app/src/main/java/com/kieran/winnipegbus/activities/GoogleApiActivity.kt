package com.kieran.winnipegbus.activities

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.preference.PreferenceManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.interfaces.Logger

abstract class GoogleApiActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected var googleApiClient: GoogleApiClient? = null
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    val isGooglePlayServicesAvailable: Boolean
        get() {
            val resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
            return ConnectionResult.SUCCESS == resultCode
        }

    val nearbyStopsDistance: Int
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            return prefs.getString(getString(R.string.pref_key_nearby_distance), "200")?.toInt()!!
        }



    protected fun connectClient() {
        if (isGooglePlayServicesAvailable && googleApiClient != null) {
            googleApiClient!!.connect()
        }
    }

    override fun onStart() {
        super.onStart()
        connectClient()
    }

    override fun onStop() {
        if (googleApiClient != null) {
            googleApiClient!!.disconnect()
        }
        super.onStop()
    }

    override fun onConnectionSuspended(i: Int) {
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            showShortToaster("Disconnected. Please re-connect.")
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            showShortToaster("Network lost. Please re-connect.")
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST)
            } catch (ex: IntentSender.SendIntentException) {
                Logger.getLogger().error(ex, "Error connecting to google API")
            }

        } else {
            showLongToaster("Sorry. Google services not available to you")
        }
    }

    protected fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                startLocationUpdates()
        }
    }

    fun requestLatestLocation(function: (location: Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
            return
        }

        val lastLocation = fusedLocationClient.lastLocation
        lastLocation.addOnSuccessListener(function)
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(LocationRequest.PRIORITY_HIGH_ACCURACY,
            10000).build()
        fusedLocationClient.requestLocationUpdates(locationRequest,
            { location: Location? ->
                latestLocation = location
            },
            Looper.getMainLooper())
    }

    companion object {
        private val FINE_LOCATION = 1
        protected val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        val LOCATION_REFRESH_INTERVAL = 30000
        val LOCATION_SERVICES_NOT_AVAILABLE = "Location services are not enabled"
        val ACQUIRING_LOCATION = "Acquiring Location..."
        var latestLocation: Location? = null
    }
}
