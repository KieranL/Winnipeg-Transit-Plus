package com.kieran.winnipegbus.Activities;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.kieran.winnipegbus.R;

public abstract class GoogleApiActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected GoogleApiClient googleApiClient;
    protected final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int LOCATION_REFRESH_INTERVAL = 30000;

    protected void connectClient() {
        if (isGooglePlayServicesAvailable() && googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    public boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return ConnectionResult.SUCCESS == resultCode;
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            showShortToaster("Disconnected. Please re-connect.");
        } else if (i == CAUSE_NETWORK_LOST) {
            showShortToaster("Network lost. Please re-connect.");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            showLongToaster("Sorry. Google services not available to you");
        }
    }

    public int getNearbyStopsDistance() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return Integer.parseInt(prefs.getString(getString(R.string.pref_key_nearby_distance), "100"));
    }

    protected LocationRequest requestLocation() {
        LocationRequest locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(LOCATION_REFRESH_INTERVAL);
        locationRequest.setFastestInterval(LOCATION_REFRESH_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
        return locationRequest;
    }
}
