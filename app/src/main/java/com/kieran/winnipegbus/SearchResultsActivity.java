package com.kieran.winnipegbus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.enums.SearchQueryTypeIds;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class SearchResultsActivity extends AppCompatActivity {
    private List<FavouriteStop> searchResultsList = new ArrayList<>();
    private StopListAdapter adapter;
    private SearchQuery searchQuery;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_list);

        ListView listView = (ListView) findViewById(R.id.stops_listView);
        adView = (AdView) findViewById(R.id.stopsListAdView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FavouriteStop stop = (FavouriteStop) parent.getItemAtPosition(position);
                openStopTimes(stop.getStopNumber());
            }
        });

        adapter = new StopListAdapter(this, R.layout.listview_stops_row, searchResultsList);
        listView.setAdapter(adapter);

        String s = getIntent().getStringExtra(HomeScreenActivity.SEARCH_QUERY).trim();

        searchQuery = BusUtilities.generateSearchQuery(s);

        updateTitle();
        adView = ActivityUtilities.initializeAdsIfEnabled(this, adView);
        new LoadSearchResults().execute(searchQuery.getQueryUrl());
    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adView = ActivityUtilities.destroyAdView(adView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        return true;
    }

    private void updateTitle() {
        if(searchQuery.getSearchQueryTypeId() == SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId)
            setTitle("Results for Route " + searchQuery.getQuery());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                ActivityUtilities.openSettings(this);
                return true;
            case R.id.favourites:
                ActivityUtilities.openFavourites(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openStopTimes(int stopNumber) {
        Intent intent = new Intent(this, StopTimesActivity.class);

        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        startActivity(intent);
    }



    private class LoadSearchResults extends AsyncTask<String, Void, LoadResult> {
        @Override
        protected LoadResult doInBackground(String... urls) {
                return BusUtilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                NodeList stops = ((Document)result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                if(stops.getLength() > 0) {
                    for (int s = 0; s < stops.getLength(); s++) {
                        Node stop = stops.item(s);
                        searchResultsList.add(new FavouriteStop(BusUtilities.getValue(StopTimesNodeTags.STOP_NAME.tag, stop), Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, stop))));
                    }

                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.no_results_found, Toast.LENGTH_LONG).show();
                }
            }else if(result.getException() != null) {
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
