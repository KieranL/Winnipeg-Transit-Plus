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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import winnipegbusbackend.FavouriteStop;
import winnipegbusbackend.FavouriteStopsList;

public class FavouritesActivity extends AppCompatActivity {
    private FavouriteStopAdapter adapter;
    private List<FavouriteStop> favouriteStops;
    private int sortTypeId;

    @Override
    public void onRestart() {
        super.onRestart();

        favouriteStops.clear();
        getFavouritesList();
        adapter.notifyDataSetChanged();
    }

    private void initialize() {
        setContentView(R.layout.activity_favourites);
        FavouriteStopsList.loadFavourites();
        favouriteStops = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.favourites_listview);

        getFavouritesList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int stopNumber = Integer.parseInt((((TextView) view.findViewById(R.id.favourites_stop_number)).getText().toString()));
                openStopTimes(stopNumber);
            }
        });

        adapter = new FavouriteStopAdapter(this, R.layout.listview_favourite_stop_row, favouriteStops);
        listView.setAdapter(adapter);
    }

    private void getSortPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortTypeId = Integer.parseInt(prefs.getString("pref_favourites_sort_list", "0"));
    }

    private void getFavouritesList() {
        getSortPreference();
        favouriteStops.addAll(FavouriteStopsList.getFavouriteStopsSorted(sortTypeId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
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
