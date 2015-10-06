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

import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.Adapters.StopTimeAdapter;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.RouteSchedule;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.StopTime;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class StopTimesActivity extends AppCompatActivity {

    private Stop stop;
    private int stopNumber;
    private String stopName;
    private boolean loading = true;
    private List<ScheduledStop> stops = new ArrayList<>();
    private ListView listView;
    private StopTimeAdapter adapter;
    private LayoutInflater inflater;
    private AdView adView;
    private MenuItem refreshIcon;
    private List<Integer> routeNumberFilter = new ArrayList<>();
    private TextView title;
    private boolean[] selectedRoutes;
    private Context context;
    private List<RouteSchedule> routeFilterRoutes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);
        context = this;

        FavouriteStopsList.loadFavourites();

        listView = (ListView) findViewById(R.id.stop_times_listview);
        adView = (AdView) findViewById(R.id.stopTimesAdView);

        adView = ActivityUtilities.initializeAdsIfEnabled(this, adView);
        createListViewListener();

        adapter = new StopTimeAdapter(this, R.layout.listview_stop_times_row, stops);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_stop_times_header, null));
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        stopNumber = intent.getIntExtra(HomeScreenActivity.STOP_NUMBER, 0);
        setTitle("Stop " + Integer.toString(stopNumber));

        getTimes();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        adView = ActivityUtilities.destroyAdView(adView);
        loading = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
        loading = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    private void createListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void getTimes() {
        String urlPath = BusUtilities.generateStopNumberURL(stopNumber, routeNumberFilter, getScheduleEndTime());
        new LoadStopTimes().execute(urlPath);
    }

    private StopTime getScheduleEndTime() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StopTime endTime = new StopTime(new Date());
        endTime.increaseHour(Byte.parseByte(prefs.getString("pref_schedule_load_interval", "2")));

        return endTime;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);
        refreshIcon = menu.findItem(R.id.refresh_button);
        refresh();

        if (FavouriteStopsList.contains(stopNumber))
            menu.findItem(R.id.add_to_favourites_button).setIcon(R.drawable.ic_favorite_stops);

        return true;
    }

    private void refresh() {
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
                    refreshIcon.getActionView().clearAnimation();
                    refreshIcon.setActionView(null);
                }
            }
        });

        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        refreshIcon.setActionView(iv);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_button:
                loading = true;
                refresh();
                adapter.getTimeSetting();
                getTimes();
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
                }else {
                    ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
                }
                return true;
            case R.id.settings:
                ActivityUtilities.openSettings(this);
                return true;
            case android.R.id.home:
                finish();
                return true;
            case R.id.filter_button:
                if(!loading && stop != null)
                    openFilterWindow();
                else
                    ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void openFilterWindow() {
        if(stop != null) {
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(this);
            filterDialog.setTitle(R.string.filter_dialog_title);
            if (routeFilterRoutes.size() == 0)
                getFilterRoutes();

            if(routeFilterRoutes.size() < stop.getRouteList().size()) {
                routeFilterRoutes.clear();
                getFilterRoutes();
            }

            CharSequence charSequence[] = new CharSequence[routeFilterRoutes.size()];

            if (selectedRoutes == null)
                selectedRoutes = new boolean[routeFilterRoutes.size()];

            for (int i = 0; i < charSequence.length; i++)
                charSequence[i] = routeFilterRoutes.get(i).toString();

            filterDialog.setMultiChoiceItems(charSequence, selectedRoutes, new AlertDialog.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    int routeNumber = routeFilterRoutes.get(which).getRouteNumber();
                    selectedRoutes[which] = isChecked;
                    if (isChecked)
                        routeNumberFilter.add(routeNumber);
                    else
                        routeNumberFilter.remove(Integer.valueOf(routeNumber));
                }
            });

            filterDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loading = true;
                    refresh();
                    getTimes();
                }
            });
            filterDialog.setNegativeButton("Cancel", null);

            filterDialog.create().show();
        }else {
            ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
        }
    }

    public void getFilterRoutes() {
        for(RouteSchedule routeSchedule : stop.getRouteList())
            routeFilterRoutes.add(new RouteSchedule(routeSchedule));

        Collections.sort(routeFilterRoutes);
    }

    private class LoadStopTimes extends AsyncTask<String, Void, LoadResult> {
        @Override
        protected LoadResult doInBackground(String... urls) {
                return BusUtilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (loading && result.getResult() != null) {
                if(stop == null) {
                    stop = new Stop((Document) result.getResult(), stopNumber);
                    stop.loadRoutes();
                    stopName = stop.getName();
                }else {
                    stop.refresh((Document) result.getResult());
                }
                stops.clear();
                stops.addAll(stop.getScheduledStopsSorted());
                if(stops.size() == 0)
                    ActivityUtilities.createLongToaster(context, getText(R.string.no_results_found).toString());
            }else if(result.getException() != null) {
                ActivityUtilities.createLongToaster(context, getText(R.string.network_error).toString());
            }

            finishLoading();
        }

        private void finishLoading() {
            if(title == null)
                title = ((TextView) findViewById(R.id.listview_stop_times_header_text));
            title.setText(stopName);

            adapter.notifyDataSetChanged();
            loading = false;
        }
    }
}