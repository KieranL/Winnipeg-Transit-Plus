package com.kieran.winnipegbus.Activities;

import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kieran.winnipegbus.ActivityUtilities;
import com.kieran.winnipegbus.Adapters.StopFeaturesAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopFeatures;

import org.w3c.dom.Document;

public class StopInfoActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private StopFeatures stopFeatures;
    private StopFeaturesAdapter adapter;
    private AsyncTask task;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adViewResId = R.id.stopFeaturesAdView;
        setContentView(R.layout.activity_stop_info);
        stopFeatures = (StopFeatures) getIntent().getSerializableExtra(StopTimesActivity.STOP);
        setTitle("Stop " + stopFeatures.getStopNumber());
        ((TextView)findViewById(R.id.stop_features_title)).setText(stopFeatures.getStopName());

        ListView listView = (ListView) findViewById(R.id.listView_stop_features);
        adapter = new StopFeaturesAdapter(this, R.layout.listview_stop_features_row, stopFeatures.getStopFeatures());
        listView.setAdapter(adapter);

        initializeAdsIfEnabled();
        task = new LoadStopFeatures().execute(BusUtilities.generateStopFeaturesUrl(stopFeatures.getStopNumber()));
        mapOnCreate();
    }

    public void mapOnCreate(){
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                }
            });
        }
    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            map.setMyLocationEnabled(true);

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        }
    }

    protected void connectClient() {
         //Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private boolean isGooglePlayServicesAvailable() {
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
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_info, menu);
        return true;
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }



    @Override
    public void onConnected(Bundle dataBundle) {
        final Context context = this;
        LatLng latLng = new LatLng(stopFeatures.getCoordinates().getLatitude(), stopFeatures.getCoordinates().getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.moveCamera(cameraUpdate);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        map.addMarker(markerOptions);
        map.setTrafficEnabled(true);
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(isLocationEnabled()) {
                    return false;
                }else {
                    ActivityUtilities.createLongToaster(context, "Location services are disabled");
                    return true;
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    private void showStopFeatures() {
        if(stopFeatures.getNumberofFeatures() > 0) {
            adapter.notifyDataSetChanged();
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.stop_features_group);
            layout.setVisibility(View.VISIBLE);
        }
    }

    private class LoadStopFeatures extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                stopFeatures.clearStopFeatures();
                stopFeatures.loadFeatures((Document) result.getResult());
                showStopFeatures();
            }else if(result.getException() != null) {
                ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.network_error).toString());
            }

        }

    }
}
