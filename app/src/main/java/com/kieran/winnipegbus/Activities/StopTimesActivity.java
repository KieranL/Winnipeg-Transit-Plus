package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kieran.winnipegbus.ActivityUtilities;
import com.kieran.winnipegbus.Adapters.StopTimeAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.ShakeDetector;
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
import java.util.List;

public class StopTimesActivity extends BaseActivity {

    public static final String FILTER_POSITIVE = "Done";
    private Stop stop;
    private int stopNumber;
    private String stopName;
    private boolean loading = true;
    private List<ScheduledStop> stops = new ArrayList<>();
    private ListView listView;
    private StopTimeAdapter adapter;
    private MenuItem refreshIcon;
    private List<Integer> routeNumberFilter = new ArrayList<>();
    private TextView title;
    private boolean[] selectedRoutes;
    private List<RouteSchedule> routeFilterRoutes = new ArrayList<>();
    public final static String STOP = "stop";
    public static ScheduledStop selectedStop;
    private AsyncTask loadStopTimesTask;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    private boolean hasFilterChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);
        adViewResId = R.id.stopTimesAdView;

        FavouriteStopsList.loadFavourites();

        listView = (ListView) findViewById(R.id.stop_times_listview);

        createListViewListener();

        adapter = new StopTimeAdapter(this, R.layout.listview_stop_times_row, stops);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_stop_times_header, null));
        listView.setAdapter(adapter);

        Intent intent = getIntent();
        stopNumber = intent.getIntExtra(HomeScreenActivity.STOP_NUMBER, 0);
        setTitle("Stop " + Integer.toString(stopNumber));

        initializeAdsIfEnabled();
        getTimes();
        createShakeListener();
    }

    public void createShakeListener() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                refresh();
            }
        });
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

    private void createListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openAdditionalInfo(position);
            }
        });
    }

    private void openAdditionalInfo(int position) {
        Intent intent;
        if (stop != null) {
            if (position == 0) {
                intent = new Intent(this, StopInfoActivity.class);
                intent.putExtra(STOP, stop.createStopFeatures());
            } else {
                selectedStop = stops.get(position - 1);
                intent = new Intent(this, ScheduledStopInfoActivity.class);
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
        endTime.increaseHour(Byte.parseByte(prefs.getString(getString(R.string.pref_schedule_load_interval), "2")));

        return endTime;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);
        refreshIcon = menu.findItem(R.id.action_refresh);

        refreshIcon.setActionView(R.layout.iv_refresh);

        refreshIcon.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.performIdentifierAction(refreshIcon.getItemId(), 0);
            }
        });

        startLoadingAnimation(refreshIcon);

        menu.findItem(R.id.add_to_favourites_button).setIcon(getFavouritesButtonDrawable(FavouriteStopsList.contains(stopNumber)));

        return true;
    }

    private Drawable getFavouritesButtonDrawable(boolean isFavoured) {
        int themeId = super.getThemeResId();
        if (themeId == R.style.Dark || themeId == R.style.Rt)
            if (isFavoured)
                return getResources().getDrawable(R.drawable.ic_favourite_stops_dark);
            else
                return getResources().getDrawable(R.drawable.ic_add_to_favourites_dark);
        else if (isFavoured)
            return getResources().getDrawable(R.drawable.ic_favourite_stops_light);
        else
            return getResources().getDrawable(R.drawable.ic_add_to_favourites_light);
    }

    private void startLoadingAnimation(final MenuItem animatedView) {
        final Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);

        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if (!loading && animatedView != null) {
                    animatedView.setEnabled(true);
                    animatedView.getActionView().clearAnimation();
                }
            }
        });

        rotation.setRepeatCount(Animation.INFINITE);
        if(animatedView != null) {
            animatedView.setEnabled(false);
            animatedView.getActionView().startAnimation(rotation);
        }
    }

    private void refresh() {
        loading = true;
        startLoadingAnimation(refreshIcon);
        adapter.loadTimeSetting();
        getTimes();
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
                if (!loading && stop != null)
                    openFilterWindow();
                else
                    ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void handleFavouritesClick(final MenuItem item) {
        if (FavouriteStopsList.contains(stopNumber)) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Delete this Favourite?");

            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    FavouriteStopsList.removeFromFavourites(stopNumber);
                    item.setIcon(getFavouritesButtonDrawable(false));
                }
            });

            alertDialog.setNegativeButton("No", null);
            alertDialog.create().show();
        } else if (!loading) {
            FavouriteStopsList.addToFavourites(new FavouriteStop(stopName, stopNumber));
            item.setIcon(getFavouritesButtonDrawable(true));
        } else {
            ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
        }

    }

    private void openFilterWindow() {
        if (stop != null) {
            hasFilterChanged = false;
            AlertDialog.Builder filterDialog = new AlertDialog.Builder(this);
            filterDialog.setTitle(R.string.filter_dialog_title);
            if (routeFilterRoutes.size() == 0)
                getFilterRoutes();

            if (routeFilterRoutes.size() < stop.getRouteList().size()) {
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
                    if(hasFilterChanged) {
                        loading = true;
                        startLoadingAnimation(refreshIcon);
                        getTimes();
                    }
                }
            });

            filterDialog.create().show();
        } else {
            ActivityUtilities.createLongToaster(this, getString(R.string.wait_for_load));
        }
    }

    public void getFilterRoutes() {
        for (RouteSchedule routeSchedule : stop.getRouteList())
            routeFilterRoutes.add(new RouteSchedule(routeSchedule));

        Collections.sort(routeFilterRoutes);
    }

    private class LoadStopTimes extends LoadXMLAsyncTask {
        @Override
        protected LoadResult doInBackground(String... urls) {
            LoadResult result = BusUtilities.getXML(urls[0]);

            if (loading && result.getResult() != null) {
                if (stop == null) {
                    stop = new Stop((Document) result.getResult(), stopNumber);
                    stop.loadRoutes();
                    stopName = stop.getName();
                } else {
                    stop.refresh((Document) result.getResult());
                }
                stops.clear();
                stops.addAll(stop.getScheduledStopsSorted());
            }
            return result;
        }

        @Override
        protected void onPostExecute(LoadResult result) {
            if (loading) {
                if (title == null) {
                    title = ((TextView) findViewById(R.id.listView_stop_times_header_text));
                }

                if (result.getException() != null && stop == null) {
                    ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.network_error).toString());
                    title.setText(R.string.network_error);
                } else if (stops.size() == 0) {
                    ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.no_results_found).toString());
                    title.setText(R.string.no_results_found);
                } else {
                    title.setText(stopName);
                }

                adapter.notifyDataSetChanged();
            }

            loading = false;
        }

    }
}