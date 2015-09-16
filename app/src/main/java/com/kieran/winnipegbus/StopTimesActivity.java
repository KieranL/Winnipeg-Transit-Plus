package com.kieran.winnipegbus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStopInfo;
import com.kieran.winnipegbusbackend.Stop;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;


public class StopTimesActivity extends AppCompatActivity {

    private BusUtilities utilities;
    private String urlPath;
    private int stopNumber;
    private String stopName;
    private boolean loading;
    private List<ScheduledStopInfo> stops = new ArrayList<>();
    private ListView listView;
    private StopTimeAdapter adapter;
    private LayoutInflater inflater;
    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);

        adView = (AdView) findViewById(R.id.stopTimesAdView);
        utilities = new BusUtilities();
        FavouriteStopsList.loadFavourites();
        initializeAdsIfEnabled();

        listView = (ListView) findViewById(R.id.stop_times_listview);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        adapter = new StopTimeAdapter(this, R.layout.listview_stop_times_row, stops);

        listView.setAdapter(adapter);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_stop_times_header, null));

        getTimes();
    }

    private void initializeAdsIfEnabled() {
        if (!areAdsDisabled()) {
            adView.setVisibility(View.VISIBLE);
            createAd();
        } else {
            adView.setVisibility(View.GONE);
        }
    }

    private boolean areAdsDisabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("pref_ads_disabled", false);
    }

    private void getTimes() {
        loading = true;

        Intent intent = getIntent();
        stopNumber = intent.getIntExtra(HomeScreenActivity.STOP_NUMBER, 0);
        setTitle("Stop " + Integer.toString(stopNumber));

        urlPath = utilities.generateStopNumberURL(stopNumber, utilities.getIntegerArrayFromString(intent.getStringExtra(HomeScreenActivity.ROUTE_NUMBER)));

        loadTimes();
    }

    private void createAd() {
        AdView mAdView = (AdView) findViewById(R.id.stopTimesAdView);
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(getString(R.string.test_device_id_gs5));
        mAdView.loadAd(adRequest.build());
    }

    private void loadTimes() {
        new LoadStopTimes().execute(urlPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);

        refresh(menu.findItem(R.id.refresh_button));

        if (FavouriteStopsList.contains(stopNumber))
            menu.findItem(R.id.add_to_favourites_button).setIcon(R.drawable.ic_favorite_stops);
        return true;
    }

    private void refresh(final MenuItem item) {
        if(inflater == null)
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ImageView iv = (ImageView) inflater.inflate(R.layout.iv_refresh, null);
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
                    adapter.notifyDataSetChanged();
                    item.getActionView().clearAnimation();
                    item.setActionView(null);
                }
            }
        });

        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        item.setActionView(iv);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_button:
                refresh(item);
                loadTimes();
                return true;
            case R.id.add_to_favourites_button:
                if (FavouriteStopsList.contains(stopNumber) && !loading) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setMessage("Delete this Favourite?");

                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int which) {
                            FavouriteStopsList.removeFromFavourites(stopNumber);
                            item.setIcon(R.drawable.ic_add_to_favourites);
                        }
                    });

                    alertDialog.setNegativeButton("No", null);
                    alertDialog.create().show();
                } else if (!loading) {
                    FavouriteStopsList.addToFavourites(new FavouriteStop(stopName, stopNumber));
                    item.setIcon(R.drawable.ic_favorite_stops);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class LoadStopTimes extends AsyncTask<String, Void, LoadResult> {
        @Override
        protected LoadResult doInBackground(String... urls) {
                return utilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                Stop stop = new Stop((Document)result.getResult(), stopNumber);
                stop.loadRoutes();
                stop.loadScheduledStops();
                stopName = stop.getName();
                stops.clear();
                stops.addAll(stop.getScheduledStopInfosSorted());
                if(stops.size() == 0)
                    Toast.makeText(getApplicationContext(), R.string.no_results_found, Toast.LENGTH_LONG).show();
            }else if(result.getException() != null) {
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }

            finishLoading();
        }

        private void finishLoading() {
            ((TextView) findViewById(R.id.listview_stop_times_header_text)).setText(stopName);
            loading = false;
        }

    }

}