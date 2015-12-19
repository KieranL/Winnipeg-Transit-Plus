package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.kieran.winnipegbus.ActivityUtilities;
import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends BaseActivity {
    private StopListAdapter adapter;
    private List<FavouriteStop> favouriteStops;
    private int sortTypeId;

    @Override
    public void onRestart() {
        super.onRestart();
        reloadList();
    }

    private void reloadList() {
        favouriteStops.clear();
        FavouriteStopsList.isLoadNeeded = true;

        if(FavouriteStopsList.length() == 0)
            FavouriteStopsList.isLoadNeeded = true;

        getFavouritesList();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adViewResId = R.id.stopsListAdView;

        setContentView(R.layout.activity_stops_list);

        favouriteStops = new ArrayList<>();

        ListView listView = (ListView) findViewById(R.id.stops_listView);

        initializeAdsIfEnabled();
        getFavouritesList();

        createListViewListeners(listView);

        adapter = new StopListAdapter(this, R.layout.listview_stops_row, favouriteStops);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createListViewListeners(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteStop stop = (FavouriteStop)parent.getItemAtPosition(position);
                openStopTimes(stop.getStopNumber());
            }
        });

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                alertDialog.setMessage("Delete this Favourite?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        FavouriteStopsList.removeFromFavourites(adapter.getItem(position).getStopNumber());
                        reloadList();
                    }
                });

                alertDialog.setNegativeButton("No", null);
                alertDialog.create().show();

                return true;
            }
        });
    }

    private void getSortPreference() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sortTypeId = Integer.parseInt(prefs.getString(getString(R.string.pref_favourites_sort), "0"));
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

    private void openStopTimes(int stopNumber) {
        try{
            FavouriteStopsList.getFavouriteStopByStopNumber(stopNumber).use();
            FavouriteStopsList.saveFavouriteStops();
        } catch (Exception e) {}

        ActivityUtilities.openStopTimes(this, stopNumber);
    }
}
