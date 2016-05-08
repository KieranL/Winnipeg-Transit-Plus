package com.kieran.winnipegbus.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.kieran.winnipegbus.AppRater;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.Stop;

public class HomeScreenActivity extends GoogleApiActivity implements LocationListener {
    public static final String LOCATION_SERVICES_NOT_AVAILABLE = "Location services are not enabled";
    public static final String ACQUIRING_LOCATION = "Acquiring Location...";
    private Button searchButton;
    private EditText searchField;

    @Override
    protected void onRestart() {
        super.onRestart();

        updateGoButtonStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_screen);
        adViewResId = R.id.homeScreenAdView;

        AppRater rater = new AppRater(this);
        rater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now_button, R.string.rate_later_button, R.string.rate_never_button);
        rater.show();

        searchButton = (Button) findViewById(R.id.search_button);

        searchField = (EditText) findViewById(R.id.search_field);

        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (isSearchEnabled() && keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    submitSearch(v);
                }
                return false;
            }
        });


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateGoButtonStatus();
            }
        };

        initializeAdsIfEnabled();
        searchButton.setEnabled(false);
        searchField.addTextChangedListener(watcher);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        connectClient();
    }

    private void updateGoButtonStatus() {
        searchButton.setEnabled(isSearchEnabled());
    }

    private boolean isSearchEnabled() {
        return searchField.getText().length() > 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourites:
                super.openFavourites();
                return true;
            case R.id.nearby_stops:
                startNearbyStopsActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitSearch(View view) {
        try{
            int number = Integer.parseInt(searchField.getText().toString().trim());

            if(number >= 10000) {
                openStopTimes(new Stop("", number));
            }else {
                startSearchResultsActivity();
            }
        }catch (Exception e) {
           startSearchResultsActivity();
        }
    }

    private void startSearchResultsActivity() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.SEARCH_QUERY, BusUtilities.generateSearchQuery(searchField.getText().toString().trim()));
        startActivity(intent);
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocation();
    }

    private void startNearbyStopsActivity() {
        if(isLocationEnabled() && isGooglePlayServicesAvailable()) {
            Intent intent = new Intent(this, SearchResultsActivity.class);
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            if (location != null) {
                intent.putExtra(SearchResultsActivity.SEARCH_QUERY, BusUtilities.generateSearchQuery(location, getNearbyStopsDistance()));
                startActivity(intent);
            }else {
                showShortToaster(ACQUIRING_LOCATION);
            }
        }else {
            showLongToaster(LOCATION_SERVICES_NOT_AVAILABLE);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("location accuracy", Float.toString(location.getAccuracy()));
    }
}
