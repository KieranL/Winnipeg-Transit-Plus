package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.kieran.winnipegbus.Adapters.UpcomingStopsAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.NotificationData;
import com.kieran.winnipegbus.NotificationService;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.UpcomingStop;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduledStopInfoActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String EASY_ACCESS = "Easy access: %s";
    public static final String BIKE_RACK = "Bike rack: %s";
    private List<UpcomingStop> upcomingStops;
    private ScheduledStop scheduledStop;
    private boolean use24hrTime;
    private UpcomingStopsAdapter adapter;
    private List<AsyncTask> tasks;
    public static final String STOP =  "stop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_stop_info);
        scheduledStop = (ScheduledStop) getIntent().getSerializableExtra(STOP);

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

            //listView.setOnItemClickListener(this); //TODO disable to remove notification system

            fillTextViews();
            SearchQuery query = BusUtilities.generateSearchQuery(scheduledStop.getRouteKey());
            tasks.add(new LoadStopsForRoute().execute(query.getQueryUrl()));
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
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            final UpcomingStop upcomingStop = ((UpcomingStopsAdapter.StopHolder) view.getTag()).getUpcomingStop();
            alertDialog.setMessage("Create a notification for this bus at " + upcomingStop.toString() + "?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {

                    NotificationData notificationData = upcomingStop.toNotificationData(scheduledStop.getRouteNumber(), scheduledStop.getRouteVariantName(), getApplicationContext(), scheduledStop.getCoverageType());
                    NotificationService.createNotification(notificationData);
                }
            });

            alertDialog.setNegativeButton("No", null);
            alertDialog.create().show();
        }
    }

    public class LoadStopsForRoute extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                NodeList stops = ((Document)result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                if(stops.getLength() > 0) {
                    int stopNumber;
                    for (int s = 0; s < stops.getLength(); s++) {
                        Node stop = stops.item(s);
                        stopNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, stop));

                        try {
                            tasks.add(new LoadStopTimes().executeOnExecutor(THREAD_POOL_EXECUTOR, BusUtilities.generateStopNumberURL(stopNumber, scheduledStop.getRouteNumber(), scheduledStop.getEstimatedDepartureTime(), null)));
                        }catch (Exception e) {
                            Log.e("Task", "task error");
                        }
                    }
                }
            }else if(result.getException() != null) {
                showLongToaster(getText(R.string.network_error).toString());
                Log.e("ERRor", result.getException().getMessage());
            }
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
                    Collections.sort(upcomingStops);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}