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
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.kieran.winnipegbus.Adapters.StopListAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.enums.SearchQueryType;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class SearchResultsActivity extends GoogleApiActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LocationListener {
    public static final String SEARCH_QUERY = "search_query";
    public static List<FavouriteStop> searchResultsList = new ArrayList<>();
    private StopListAdapter adapter;
    private SearchQuery searchQuery;
    private boolean loading = false;
    private AsyncTask task;
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchResultsList.clear();
        adViewResId = R.id.stopsListAdView;
        setContentView(R.layout.activity_search_results);

        listView = (ListView) findViewById(R.id.stops_listView);

        listView.setOnItemClickListener(this);

        adapter = new StopListAdapter(this, R.layout.listview_stops_row, searchResultsList);
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
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.search_results_swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.rt_blue, R.color.rt_red);

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
                showLongToaster(getText(R.string.network_error).toString());
            }
        }
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            setTitle("Stops on Rte " + searchQuery.getQuery());
        else if(searchQuery.getSearchQueryType() == SearchQueryType.NEARBY)
            setTitle("Nearby Stops");
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
                    showLongToaster(getString(R.string.wait_for_load));
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
                FavouriteStopsList.addToFavourites(searchResultsList.get(position));
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
                    searchResultsList.clear();
                    NodeList stops = ((Document) result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                    if (stops.getLength() > 0) {
                        for (int s = 0; s < stops.getLength(); s++) {
                            Node stop = stops.item(s);
                            FavouriteStop favouriteStop = new FavouriteStop(BusUtilities.getValue(StopTimesNodeTags.STOP_NAME.tag, stop), Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, stop)));
                            searchResultsList.add(favouriteStop);

                                favouriteStop.setLatLng(getLatLng(stop));

                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_results_found, Toast.LENGTH_LONG).show();
                    }
                } else if (result.getException() != null) {
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }

            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }

    private LatLng getLatLng(Node stop) {
        return new LatLng(Double.parseDouble(BusUtilities.getValue(StopTimesNodeTags.LATITUDE.tag, stop)), Double.parseDouble(BusUtilities.getValue(StopTimesNodeTags.LONGITUDE.tag, stop)));
    }
}
