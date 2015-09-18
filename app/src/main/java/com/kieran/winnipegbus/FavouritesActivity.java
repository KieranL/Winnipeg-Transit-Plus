package com.kieran.winnipegbus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {
    private StopListAdapter adapter;
    private List<FavouriteStop> favouriteStops;
    private int sortTypeId;
    private AdView adView;

    @Override
    public void onRestart() {
        super.onRestart();
        initializeAdsIfEnabled();
        favouriteStops.clear();
        getFavouritesList();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    private void initialize() {
        setContentView(R.layout.activity_stops_list);
        adView = (AdView) findViewById(R.id.favouritesAdView);
        initializeAdsIfEnabled();

        favouriteStops = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.stops_listView);

        getFavouritesList();

        createListViewListener(listView);

        adapter = new StopListAdapter(this, R.layout.listview_stops_row, favouriteStops);
        listView.setAdapter(adapter);
    }

    private void initializeAdsIfEnabled() {
        if (!areAdsDisabled()) {
            adView.setVisibility(View.VISIBLE);
            createAd();
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    private void createListViewListener(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteStop stop = (FavouriteStop)parent.getItemAtPosition(position);
                openStopTimes(stop.getStopNumber());
            }
        });
    }

    private boolean areAdsDisabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("pref_ads_disabled", false);
    }

    private void createAd() {
        AdView mAdView = (AdView) findViewById(R.id.favouritesAdView);
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(getString(R.string.test_device_id_gs5));
        mAdView.loadAd(adRequest.build());
    }

    private void getSortPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortTypeId = Integer.parseInt(prefs.getString("pref_favourites_sort_list", "0"));
    }

    private void getFavouritesList() {
        FavouriteStopsList.loadFavourites();
        getSortPreference();
        favouriteStops.addAll(FavouriteStopsList.getFavouriteStopsSorted(sortTypeId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openStopTimes(int stopNumber) {
        Intent intent = new Intent(this, StopTimesActivity.class);

        try{
            FavouriteStopsList.getFavouriteStopByStopNumber(stopNumber).use();
            FavouriteStopsList.saveFavouriteStops();
        } catch (Exception e) {}


        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        intent.putExtra(HomeScreenActivity.ROUTE_NUMBER, new int[]{});
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
