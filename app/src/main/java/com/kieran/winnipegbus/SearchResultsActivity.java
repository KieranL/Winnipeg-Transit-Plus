package com.kieran.winnipegbus;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


public class SearchResultsActivity extends BaseActivity {
    private List<FavouriteStop> searchResultsList = new ArrayList<>();
    private StopListAdapter adapter;
    private SearchQuery searchQuery;
    private AdView adView;
    private boolean loading;
    private AsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading = true;
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
        ActivityUtilities.initializeAdsIfEnabled(this, adView);
       task = new LoadSearchResults().execute(searchQuery.getQueryUrl());
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
        task.cancel(true);
        ActivityUtilities.destroyAdView(adView);
        loading = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
        MenuItem item = menu.findItem(R.id.loadingIcon);
        item.setVisible(true);

        item.setActionView(R.layout.iv_refresh);
        startLoadingAnimation(item);

        return true;
    }

    private void startLoadingAnimation(final MenuItem animatedView) {
        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);

        rotation.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(!loading) {
                    animatedView.setEnabled(true);
                    animatedView.getActionView().clearAnimation();
                    animatedView.setVisible(false);
                }
            }
        });

        rotation.setRepeatCount(Animation.INFINITE);
        animatedView.setEnabled(false);
        animatedView.getActionView().startAnimation(rotation);
    }

    private void updateTitle() {
        if(searchQuery.getSearchQueryTypeId() == SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId)
            setTitle("Results for Route " + searchQuery.getQuery());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourites:
                super.openFavourites();
                return true;
            case android.R.id.home:
                finish();
                HomeScreenActivity.reCreate();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeScreenActivity.reCreate();
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
            if(loading) {
                if (result.getResult() != null) {
                    NodeList stops = ((Document) result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                    if (stops.getLength() > 0) {
                        for (int s = 0; s < stops.getLength(); s++) {
                            Node stop = stops.item(s);
                            searchResultsList.add(new FavouriteStop(BusUtilities.getValue(StopTimesNodeTags.STOP_NAME.tag, stop), Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, stop))));
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_results_found, Toast.LENGTH_LONG).show();
                    }
                } else if (result.getException() != null) {
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }


            loading = false;
        }
    }
}
