package com.kieran.winnipegbus.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kieran.winnipegbus.Adapters.UpcomingStopsAdapter;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.Views.StyledSwipeRefresh;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.UpcomingStop;
import com.kieran.winnipegbusbackend.UpcomingStops.HttpUpcomingStopsManager;
import com.kieran.winnipegbusbackend.UpcomingStops.UpcomingStopsManager;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduledStopInfoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, TransitApiManager.OnJsonLoadResultReceiveListener, UpcomingStopsManager.OnUpcomingStopsFoundListener {
    public static final String EASY_ACCESS = "Easy access: %s";
    public static final String BIKE_RACK = "Bike rack: %s";
    private List<UpcomingStop> upcomingStops;
    private ScheduledStop scheduledStop;
    private boolean use24hrTime;
    private UpcomingStopsAdapter adapter;
    private List<AsyncTask> tasks;
    public static final String STOP_EXTRA = "stop";
    private boolean loading = false;
    private StyledSwipeRefresh swipeRefreshLayout;
    private UpcomingStopsManager upcomingStopsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_stop_info);
        scheduledStop = (ScheduledStop) getIntent().getSerializableExtra(STOP_EXTRA);
        upcomingStopsManager = new HttpUpcomingStopsManager();

        if (scheduledStop != null) {
            use24hrTime = getTimeSetting();
            upcomingStops = new ArrayList<>();

            ListView listView = ((ListView) findViewById(R.id.listView_upcoming_stops));
            View headerView = getLayoutInflater().inflate(R.layout.listview_upcoming_stops_header, null);
            headerView.setEnabled(false);
            headerView.setClickable(false);
            listView.addHeaderView(headerView);

            adapter = new UpcomingStopsAdapter(this, R.layout.upcoming_stops_row, upcomingStops, use24hrTime);
            listView.setAdapter(adapter);
            tasks = new ArrayList<>();

            swipeRefreshLayout = (StyledSwipeRefresh) findViewById(R.id.upcoming_stops_swipeRefresh);
            swipeRefreshLayout.setOnRefreshListener(this);

            fillTextViews();
        }else {
            finish();
        }
    }

    public void onResume(){
        super.onResume();
        use24hrTime = getTimeSetting();
        fillTextViews();
    }

    public void onDestroy() {
        super.onDestroy();

        if(tasks != null)
            for (AsyncTask task : tasks)
                task.cancel(true);
    }

    private void fillTextViews() {
        if(scheduledStop != null) {
            TextView routeNumber = getTextView(R.id.bus_number);
            routeNumber.setText(Integer.toString(scheduledStop.getRouteNumber()));
            BaseActivity.setTextViewColour(this, routeNumber, scheduledStop);

            setTextViewText(R.id.bus_name, scheduledStop.getRouteVariantName());

            if (scheduledStop.hasArrivalTime()) {
                findViewById(R.id.arrival_times_header).setVisibility(View.VISIBLE);

                TextView arrival = getTextView(R.id.scheduled_arrival);
                arrival.setText(scheduledStop.getScheduledArrivalTime().toFormattedString(null, use24hrTime));
                arrival.setVisibility(View.VISIBLE);

                TextView departure = getTextView(R.id.estimated_arrival);
                departure.setText(scheduledStop.getEstimatedArrivalTime().toFormattedString(null, use24hrTime));
                departure.setVisibility(View.VISIBLE);
            }

            setTextViewText(R.id.scheduled_departure, scheduledStop.getScheduledDepartureTime().toFormattedString(null, use24hrTime));
            setTextViewText(R.id.estimated_departure, scheduledStop.getEstimatedDepartureTime().toFormattedString(null, use24hrTime));

            setTextViewText(R.id.has_bike_rack, String.format(BIKE_RACK, booleanStringValue(scheduledStop.hasBikeRack())));
            setTextViewText(R.id.has_easy_access, String.format(EASY_ACCESS, booleanStringValue(scheduledStop.hasEasyAccess())));
        }
    }

    private String booleanStringValue(boolean b) {
        return b ? "Yes" : "No";
    }

    private TextView getTextView(int id) {
        return (TextView)findViewById(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scheduled_stop_info, menu);

        onOptionsItemSelected(menu.findItem(R.id.action_refresh));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        if (!loading) {
            if (isOnline()) {
                loading = true;
                upcomingStops.clear();

                upcomingStopsManager.GetUpcomingStopsAsync(scheduledStop.getRouteKey(), scheduledStop.getKey().getStopNumber(), this);
            } else {
                showLongToaster(R.string.network_error);
            }
        }
        swipeRefreshLayout.setRefreshing(loading);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void OnReceive(LoadResult<JSONObject> result) {
        if (result.getResult() != null) {
            StopSchedule stopSchedule = new StopSchedule(result.getResult());
            ScheduledStop scheduledStop1 = stopSchedule.getScheduledStopByKey(scheduledStop.getKey());

            if(scheduledStop1 != null) {
                UpcomingStop upcomingStop = new UpcomingStop(stopSchedule, scheduledStop1.getEstimatedDepartureTime(), scheduledStop1.getKey());
                upcomingStops.add(upcomingStop);
            }
        }else if(result.getException() != null) {
            handleException(result.getException());

            if(result.getException() instanceof FileNotFoundException) {
                for(AsyncTask task : tasks)
                    task.cancel(true);

                tasks.clear();
            }
        }

        if(tasks.size() <= 2) {
            Collections.sort(upcomingStops);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
        }

        removeFinishedTasks();
    }

    private void removeFinishedTasks() {
        List<AsyncTask> finishedTasks = new ArrayList<>();

        for (AsyncTask task : tasks) {
            if(task.getStatus() == AsyncTask.Status.FINISHED)
                finishedTasks.add(task);
        }

        tasks.removeAll(finishedTasks);
    }

    @Override
    public void OnUpcomingStopsFound(LoadResult<ArrayList<Integer>> result) {
        ScheduledStopInfoActivity instance = this;
        if (result.getResult() != null) {
            if (result.getResult().size() > 0) {
                for (Integer stopNumber : result.getResult()) {

                    try {
                        StopTime latest = scheduledStop.getEstimatedDepartureTime().getMilliseconds() > TransitApiManager.lastQueryTime.getMilliseconds() ? scheduledStop.getEstimatedDepartureTime() : TransitApiManager.lastQueryTime;

                        AsyncTask task = TransitApiManager.getJsonAsync(TransitApiManager.generateStopNumberURL(stopNumber, scheduledStop.getRouteNumber(), latest, null), instance);
                        tasks.add(task);
                    } catch (Exception e) {
                        Log.e("Task", "task error");
                    }
                }
            }


        } else if (result.getException() != null) {
            handleException(result.getException());

            Collections.sort(upcomingStops);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            loading = false;
            tasks.clear();
        }
    }
}