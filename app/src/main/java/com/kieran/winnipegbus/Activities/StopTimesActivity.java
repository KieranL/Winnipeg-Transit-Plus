package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kieran.winnipegbus.Adapters.StopTimeAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.NotificationService;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.ShakeDetector;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.RouteSchedule;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StopTimesActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, ShakeDetector.OnShakeListener, AdapterView.OnItemLongClickListener {

    private static final String FILTER_POSITIVE = "Done";
    public static final String STOP = "stop";
    private static final String UPDATED_STRING = "Updated %s";
    private static final String ACTIONBAR_TEXT = "Stop %d";
    private static final String DELETE_THIS_FAVOURITE = "Delete this Favourite?";
    private static final String DIALOG_YES = "Yes";
    private static final String DIALOG_NO = "No";
    private static final String CREATE_NOTIFICATION_FOR_BUS = "Create a notification for this bus?";
    private StopSchedule stopSchedule; //TODO: static?

    private int stopNumber;
    private String stopName;
    private boolean loading = false;
    private List<ScheduledStop> stops = new ArrayList<>();
    private StopTimeAdapter adapter;
    private List<Integer> routeNumberFilter = new ArrayList<>();
    private TextView title;
    private boolean[] selectedRoutes;
    private List<RouteSchedule> routeFilterRoutes = new ArrayList<>();
    private AsyncTask loadStopTimesTask;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    private boolean hasFilterChanged;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView lastUpdated;

    @Override
    protected void onNewIntent(Intent intent) {
        int newStopNumber = ((Stop) intent.getSerializableExtra(STOP)).getNumber();
        if (newStopNumber != stopNumber) {
            stopNumber = newStopNumber;
            setIntent(intent);
            recreate();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);
        adViewResId = R.id.stopTimesAdView;

        //stopSchedule = null; //TODO remove dis
        // test
        FavouriteStopsList.loadFavourites();

        ListView listView = (ListView) findViewById(R.id.stop_times_listview);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.stop_times_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.rt_blue, R.color.rt_red);

        listView.setOnItemClickListener(this);
        //listView.setOnItemLongClickListener(this); //TODO disable to remove notification system

        adapter = new StopTimeAdapter(this, R.layout.listview_stop_times_row, stops);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_stop_times_header, null));
        listView.setAdapter(adapter);

        Intent intent = getIntent();

        if (title == null) {
            title = ((TextView) findViewById(R.id.listView_stop_times_header_text));
            lastUpdated = (TextView) findViewById(R.id.stop_times_header_last_updated);
        }

        Stop stop = (Stop) intent.getSerializableExtra(STOP);
        stopName = stop.getName();
        title.setText(stopName);
        stopNumber = stop.getNumber();

        setTitle(String.format(ACTIONBAR_TEXT, stopNumber));

        initializeAdsIfEnabled();
        createShakeListener();
    }

    public void createShakeListener() {
        PackageManager PM = getPackageManager();

        if (PM.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector = new ShakeDetector(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        loading = false;
        if (loadStopTimesTask != null)
            loadStopTimesTask.cancel(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        loading = false;
        sensorManager.unregisterListener(shakeDetector);
    }

    private void openAdditionalInfo(int position) {
        Intent intent;
        if (stopSchedule != null) {
            if (position == 0) {
                intent = new Intent(this, StopInfoActivity.class);
                intent.putExtra(StopInfoActivity.STOP_FEATURES, stopSchedule.createStopFeatures());
            } else {
                intent = new Intent(this, ScheduledStopInfoActivity.class);
                intent.putExtra(ScheduledStopInfoActivity.STOP, stops.get(position - 1));
            }
            startActivity(intent);
        }

    }

    private void getTimes() {
        String urlPath = BusUtilities.generateStopNumberURL(stopNumber, routeNumberFilter, null, getScheduleEndTime());

        loadStopTimesTask = new LoadStopTimes().execute(urlPath);
    }

    private StopTime getScheduleEndTime() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        StopTime endTime = new StopTime(System.currentTimeMillis());
        endTime.increaseHour(Integer.parseInt(prefs.getString(getString(R.string.pref_schedule_load_interval), "2")));

        return endTime;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);

        menu.findItem(R.id.add_to_favourites_button).setIcon(getFavouritesButtonDrawable(FavouriteStopsList.contains(stopNumber)));
        onOptionsItemSelected(menu.findItem(R.id.action_refresh)); //manually click the refresh button, this is the only way the swipe refresh loading spinner works correctly on initial load. Not happy with this but it was the only way I could get it to work
        return true;
    }

    @SuppressWarnings("deprecation")
    private Drawable getFavouritesButtonDrawable(boolean isFavoured) {
        int themeId = super.getThemeResId();
        int drawableId;

        if (themeId == R.style.Dark || themeId == R.style.Rt)
            if (isFavoured)
                drawableId = R.drawable.ic_favourite_stops_dark;
            else
                drawableId = R.drawable.ic_add_to_favourites_dark;
        else if (isFavoured)
            drawableId = R.drawable.ic_favourite_stops_light;
        else
            drawableId = R.drawable.ic_add_to_favourites_light;

        return getResources().getDrawable(drawableId);
    }

    private void refresh() {
        if (!loading) {
            if (isOnline()) {
                loading = true;

                adapter.loadTimeSetting();

                getTimes();
            } else {
                showLongToaster(getText(R.string.network_error).toString());
            }
        }
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.add_to_favourites_button:
                handleFavouritesClick(item);
                return true;
            case R.id.action_filter:
                if (!loading && stopSchedule != null)
                    openFilterWindow();
                else
                    showLongToaster(getString(R.string.wait_for_load));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void handleFavouritesClick(final MenuItem item) {
        if (FavouriteStopsList.contains(stopNumber)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage(DELETE_THIS_FAVOURITE);

            alertDialog.setPositiveButton(DIALOG_YES, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    FavouriteStopsList.removeFromFavourites(stopNumber);
                    item.setIcon(getFavouritesButtonDrawable(false));
                }
            });

            alertDialog.setNegativeButton(DIALOG_NO, null);
            alertDialog.create().show();
        } else if (stopName != null && !stopName.equals("")) {
            FavouriteStopsList.addToFavourites(new FavouriteStop(stopName, stopNumber));
            item.setIcon(getFavouritesButtonDrawable(true));
        } else {
            showLongToaster(getString(R.string.wait_for_load));
        }

    }

    private void openFilterWindow() {
        if (stopSchedule != null) {
            hasFilterChanged = false;
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(this);
            filterDialog.setTitle(R.string.filter_dialog_title);

            if (routeFilterRoutes.size() == 0)
                getFilterRoutes();

            if (routeFilterRoutes.size() < stopSchedule.getRouteList().size()) {
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
                    hasFilterChanged = true;
                    int routeNumber = routeFilterRoutes.get(which).getRouteNumber();
                    selectedRoutes[which] = isChecked;
                    if (isChecked)
                        routeNumberFilter.add(routeNumber);
                    else
                        routeNumberFilter.remove(Integer.valueOf(routeNumber));
                }
            });

            filterDialog.setPositiveButton(FILTER_POSITIVE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (hasFilterChanged) {
                        refresh();
                    }
                }
            });

            filterDialog.create().show();
        } else {
            showLongToaster(getString(R.string.wait_for_load));
        }
    }

    public void getFilterRoutes() {
        for (RouteSchedule routeSchedule : stopSchedule.getRouteList())
            routeFilterRoutes.add(new RouteSchedule(routeSchedule));

        Collections.sort(routeFilterRoutes);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openAdditionalInfo(position);
    }

    @Override
    public void onShake() {
        refresh();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        if (position != 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage(CREATE_NOTIFICATION_FOR_BUS);
            alertDialog.setPositiveButton(DIALOG_YES, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    ScheduledStop selectedStop = stops.get(position - 1);
                    NotificationService.createNotification(stopNumber, selectedStop.getRouteNumber(), selectedStop.getKey(), selectedStop.getRouteVariantName(), stopName, selectedStop.getEstimatedDepartureTime(), getApplicationContext(), selectedStop.getCoverageType());
                }
            });

            alertDialog.setNegativeButton(DIALOG_NO, null);
            alertDialog.create().show();
            return true;
        }
        return false;
    }

    private class LoadStopTimes extends LoadXMLAsyncTask {
        @Override
        protected LoadResult doInBackground(String... urls) {
            LoadResult result = super.doInBackground(urls);

            if (loading && result.getResult() != null) {
                if (stopSchedule == null) {
                    stopSchedule = new StopSchedule((Document) result.getResult(), stopNumber);
                    stopName = stopSchedule.getName();
                } else {
                    stopSchedule.refresh((Document) result.getResult());
                }
                stops.clear();
                stops.addAll(stopSchedule.getScheduledStopsSorted());
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getException() != null && loading) {
                showLongToaster(getText(R.string.network_error).toString());
                if (stopSchedule == null)
                    title.setText(R.string.network_error);
            } else if (stops.size() == 0 && loading) {
                showLongToaster(getText(R.string.no_results_found).toString());
                title.setText(R.string.no_results_found);
            } else {
                title.setText(stopName);
            }

            adapter.notifyDataSetChanged();

            lastUpdated.setText(String.format(UPDATED_STRING, new StopTime(System.currentTimeMillis()).toFormattedString(null, getTimeSetting())));
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }
    }
}