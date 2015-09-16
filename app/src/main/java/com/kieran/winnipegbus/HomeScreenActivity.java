package com.kieran.winnipegbus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbusbackend.BusUtilities;


public class HomeScreenActivity extends AppCompatActivity {
    public static final String SEARCH_QUERY = "search_query";
    public static String filesDir;
    public final static String STOP_NUMBER = "stop_number";
    public final static String ROUTE_NUMBER = "route_number";
    private TextWatcher watcher;
    private Button button;
    private EditText searchField;
    private EditText routeNumberField;
    private AdView adView;

    @Override
    protected void onRestart() {
        super.onRestart();
        initializeAdsIfEnabled();

        button = (Button) findViewById(R.id.button);

        searchField = (EditText) findViewById(R.id.search_field);
        routeNumberField = (EditText) findViewById(R.id.route_number_field);

        updateGoButtonStatus();
        searchField.addTextChangedListener(watcher);
        routeNumberField.addTextChangedListener(watcher);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        filesDir = getFilesDir().getPath();

        adView = (AdView) findViewById(R.id.homeScreenAdView);
        button = (Button) findViewById(R.id.button);

        searchField = (EditText) findViewById(R.id.search_field);
        routeNumberField = (EditText) findViewById(R.id.route_number_field);

        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                updateGoButtonStatus();
            }
        };

        initializeAdsIfEnabled();
        button.setEnabled(false);
        searchField.addTextChangedListener(watcher);
        routeNumberField.addTextChangedListener(watcher);
    }

    private void initializeAdsIfEnabled() {
        if (!areAdsDisabled()) {
            createAd();
            adView.setVisibility(View.VISIBLE);
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    private boolean areAdsDisabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("pref_ads_disabled", false);
    }

    private void createAd() {
        AdView mAdView = (AdView) findViewById(R.id.homeScreenAdView);
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(getString(R.string.test_device_id_gs5));
        mAdView.loadAd(adRequest.build());
    }

    private void updateGoButtonStatus() {
        String routeNumbers = routeNumberField.getText().toString();
        boolean isRouteFilterValid = (routeNumbers.length() > 0 && new BusUtilities().getIntegerArrayFromString(routeNumbers) != null) || routeNumbers.length() == 0;
        button.setEnabled(isRouteFilterValid && searchField.getText().length() > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                openSettings();
                return true;
            case R.id.favourites:
                openFavourites();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void submitSearch(View view) {
        Intent intent;
        String routeNumberFilters = routeNumberField.getText().toString();

        try{
            intent = new Intent(this, StopTimesActivity.class);
            int stopNumber = Integer.parseInt(searchField.getText().toString());

            if(stopNumber >= 10000) {
                intent.putExtra(STOP_NUMBER, stopNumber);
                intent.putExtra(ROUTE_NUMBER, routeNumberFilters);
                startActivity(intent);
            }else {
                startSearchActivity();
            }
        }catch (Exception e) {
           startSearchActivity();
        }
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SEARCH_QUERY, searchField.getText().toString());
        startActivity(intent);
    }

    public void openFavourites() {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }
}
