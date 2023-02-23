package com.kieran.winnipegbus.activities

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.preference.PreferenceManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.interfaces.Logger

abstract class GoogleApiActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected var googleApiClient: GoogleApiClient? = null

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

    val latestLocation: Location?
        get() = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

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

    protected fun requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            validatedLocationRequest()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                validatedLocationRequest()
        }
    }

    private fun validatedLocationRequest(): LocationRequest? {
        return try {
            val locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            locationRequest.interval = LOCATION_REFRESH_INTERVAL.toLong()
            locationRequest.fastestInterval = LOCATION_REFRESH_INTERVAL.toLong()
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this as LocationListener)
            locationRequest
        } catch (ex: Exception) {
            Logger.getLogger().error(ex, "Error getting location request")
            null
        }

    }

    companion object {
        private val FINE_LOCATION = 1
        protected val CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000
        val LOCATION_REFRESH_INTERVAL = 30000
        val LOCATION_SERVICES_NOT_AVAILABLE = "Location services are not enabled"
        val ACQUIRING_LOCATION = "Acquiring Location..."
    }
}
