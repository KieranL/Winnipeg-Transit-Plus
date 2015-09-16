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

import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.SearchQueryTypeIds;
import com.kieran.winnipegbusbackend.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class SearchResultsActivity extends AppCompatActivity {
    private BusUtilities utilities = new BusUtilities();
    private List<FavouriteStop> searchResultsList = new ArrayList<>();
    private StopListAdapter adapter;
    private SearchQuery searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_list);

        ListView listView = (ListView) findViewById(R.id.stops_listView);

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

        searchQuery = utilities.generateSearchQuery(s);

        updateTitle();

        new LoadSearchResults().execute(searchQuery.getQueryUrl());
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
        int id = item.getItemId();

        if (id == R.id.settings) {
            openSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openStopTimes(int stopNumber) {
        Intent intent = new Intent(this, StopTimesActivity.class);

        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        intent.putExtra(HomeScreenActivity.ROUTE_NUMBER, new int[]{});
        startActivity(intent);
    }

    private class LoadSearchResults extends AsyncTask<String, Void, LoadResult> {
        @Override
        protected LoadResult doInBackground(String... urls) {
                return utilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                NodeList stops = ((Document)result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                if(stops.getLength() > 0) {
                    for (int s = 0; s < stops.getLength(); s++) {
                        Node stop = stops.item(s);
                        searchResultsList.add(new FavouriteStop(utilities.getValue(StopTimesNodeTags.STOP_NAME.tag, (Element) stop), Integer.parseInt(utilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, (Element) stop))));
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
