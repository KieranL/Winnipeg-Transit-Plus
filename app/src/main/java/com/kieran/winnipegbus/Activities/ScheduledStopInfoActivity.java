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
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbus.Views.StyledSwipeRefresh;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.UpcomingStop;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduledStopInfoActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final String EASY_ACCESS = "Easy access: %s";
    public static final String BIKE_RACK = "Bike rack: %s";
    private List<UpcomingStop> upcomingStops;
    private ScheduledStop scheduledStop;
    private boolean use24hrTime;
    private UpcomingStopsAdapter adapter;
    private List<AsyncTask> tasks;
    public static final String STOP_EXTRA = "stop";
    private SearchQuery query;
    private boolean loading = false;
    private StyledSwipeRefresh swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_stop_info);
        scheduledStop = (ScheduledStop) getIntent().getSerializableExtra(STOP_EXTRA);

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
            query = BusUtilities.generateSearchQuery(scheduledStop.getRouteKey());
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

    private void setTextViewText(int id, String text) {
        ((TextView)findViewById(id)).setText(text);
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
                tasks.add(new LoadStopsForRoute().execute(query.getQueryUrl()));
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

    public class LoadStopsForRoute extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                NodeList stops = ((Document)result.getResult()).getElementsByTagName(Stop.STOP_TAG);
                if(stops.getLength() > 0) {
                    int stopNumber;
                    for (int s = 0; s < stops.getLength(); s++) {
                        Node stop = stops.item(s);
                        stopNumber = Integer.parseInt(BusUtilities.getValue(Stop.STOP_NUMBER_TAG, stop));

                        try {
                            StopTime latest = scheduledStop.getEstimatedDepartureTime().getMilliseconds() > BusUtilities.lastQueryTime.getMilliseconds() ? scheduledStop.getEstimatedDepartureTime() : BusUtilities.lastQueryTime;

                            tasks.add(new LoadStopTimes().executeOnExecutor(THREAD_POOL_EXECUTOR, BusUtilities.generateStopNumberURL(stopNumber, scheduledStop.getRouteNumber(), latest, null)));
                        }catch (Exception e) {
                            Log.e("Task", "task error");
                        }
                    }
                }
            }else if(result.getException() != null) {
                showLongToaster(R.string.network_error);
            }
            tasks.remove(this);
        }
    }

    public class LoadStopTimes extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                StopSchedule stopSchedule = new StopSchedule((Document) result.getResult());
                ScheduledStop scheduledStop1 = stopSchedule.getScheduledStopByKey(scheduledStop.getKey());

                if(scheduledStop1 != null) {
                    UpcomingStop upcomingStop = new UpcomingStop(stopSchedule, scheduledStop1.getEstimatedDepartureTime(), scheduledStop1.getKey());
                    upcomingStops.add(upcomingStop);
                }
            }

            tasks.remove(this);

            if(tasks.isEmpty()) {
                Collections.sort(upcomingStops);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                loading = false;
            }
        }
    }
}