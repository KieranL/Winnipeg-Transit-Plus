package com.kieran.winnipegbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import winnipegbusbackend.FavouriteStopsList;

public class FavouritesActivity extends AppCompatActivity {
    public static List<Integer> favouritesList = FavouriteStopsList.favouritesList;
    static ListView listView;

    public static void addToFavourites(int stopNumber) {
        FavouriteStopsList.addToFavourites(stopNumber);
        FavouriteStopsList.saveFavouriteStops();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        FavouriteStopsList.loadFavourites();

        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<Integer> stopList = new ArrayAdapter<Integer>(this, R.layout.favourite_stops, favouritesList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int stopNumber = (Integer) parent.getItemAtPosition(position);
                openStopTimes(stopNumber);
            }
        });
        listView.setAdapter(stopList);

    }

    private void openStopTimes(int stopNumber) {
        Intent intent = new Intent(this, StopTimesActivity.class);

        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        intent.putExtra(HomeScreenActivity.ROUTE_NUMBER, new int[]{});
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favourites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void removeFromFavourites(int stopNumber) {
        FavouriteStopsList.removeFromFavourites(stopNumber);
        FavouriteStopsList.saveFavouriteStops();
    }
}
