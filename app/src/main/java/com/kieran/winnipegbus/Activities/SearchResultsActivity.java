package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.Views.StyledSwipeRefresh;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.SearchResults;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.enums.SearchQueryType;


public class SearchResultsActivity extends GoogleApiActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LocationListener {
    public static final String SEARCH_QUERY = "search_query";
    public static final String NEARBY_STOPS = "Nearby Stops";
    public static final String STOPS_ON_RTE = "Stops on Rte %s";
    private StopListAdapter adapter;
    private SearchQuery searchQuery;
    private boolean loading = false;
    private AsyncTask task;
    private StyledSwipeRefresh swipeRefreshLayout;
    public static SearchResults searchResults = new SearchResults();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResults.clear();
        adViewResId = R.id.stopsListAdView;
        setContentView(R.layout.activity_search_results);

        ListView listView = (ListView) findViewById(R.id.stops_listView);

        listView.setOnItemClickListener(this);

        adapter = new StopListAdapter(this, R.layout.listview_stops_row, searchResults.getStops());
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(this);

        searchQuery = (SearchQuery)getIntent().getSerializableExtra(SEARCH_QUERY);

        setupSwipeRefresh();
        setupGoogleApi();
        updateTitle();
        initializeAdsIfEnabled();

    }

    private void setupGoogleApi() {
        if(searchQuery.getSearchQueryType() == SearchQueryType.NEARBY) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            connectClient();
        }
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout = (StyledSwipeRefresh) findViewById(R.id.search_results_swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        if(searchQuery.getSearchQueryType() != SearchQueryType.NEARBY) {
            swipeRefreshLayout.setEnabled(false);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openStopTimes((Stop) parent.getItemAtPosition(position));
    }

    private void refresh() {
        if(!loading) {
            if(isOnline()) {
                loading = true;

                task = new LoadSearchResults().execute(searchQuery.getQueryUrl());
            }else {
                showLongToaster(R.string.network_error);
            }
        }
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(task != null)
            task.cancel(true);
        loading = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        if(searchQuery.getSearchQueryType() != SearchQueryType.NEARBY)
            menu.findItem(R.id.loadingIcon).setVisible(false);

        refresh();

        return true;
    }

    private void updateTitle() {
        if(searchQuery.getSearchQueryType() == SearchQueryType.ROUTE_NUMBER)
            setTitle(String.format(STOPS_ON_RTE, searchQuery.getQuery()));
        else if(searchQuery.getSearchQueryType() == SearchQueryType.NEARBY)
            setTitle(NEARBY_STOPS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourites:
                openFavourites();
                return true;
            case R.id.map:
                if (!loading)
                    openMap();
                else
                    showLongToaster(R.string.wait_for_load);
                return true;
            case R.id.loadingIcon:
                refresh();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openMap() {
        Intent intent = new Intent(this, StopsMapActivity.class);

        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setMessage("Add to Favourites?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int which) {
                FavouriteStopsList.loadFavourites();
                FavouriteStopsList.addToFavourites(searchResults.get(position));
            }
        });

        alertDialog.setNegativeButton("No", null);
        alertDialog.create().show();

        return true;
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocation();
    }

    @Override
    public void onLocationChanged(Location location) {
        searchQuery = BusUtilities.generateSearchQuery(location, getNearbyStopsDistance());
    }

    private class LoadSearchResults extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if(loading) {
                if (result.getResult() != null) {
                    searchResults.loadStops(result);

                    if (searchResults.getLength() <= 0) {
                        showLongToaster(R.string.no_results_found);
                    }
                } else if (result.getException() != null) {
                    showLongToaster(R.string.network_error);
                }
            }

            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

}
