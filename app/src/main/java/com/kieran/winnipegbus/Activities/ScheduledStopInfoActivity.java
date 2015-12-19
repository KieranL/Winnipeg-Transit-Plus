package com.kieran.winnipegbus.Activities;

import android.app.NotificationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.kieran.winnipegbus.ActivityUtilities;
import com.kieran.winnipegbus.Adapters.UpcomingStopsAdapter;
import com.kieran.winnipegbus.LoadXMLAsyncTask;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.UpcomingStop;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ScheduledStopInfoActivity extends BaseActivity {
    private Timer timer;
    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotifyMgr;
    private List<Integer> upcomingStopNumbers;
    private List<UpcomingStop> upcomingStops;
    private ScheduledStop scheduledStop;
    private boolean use24hrTime;
    private boolean loading = true;
    private UpcomingStopsAdapter adapter;
    private List<AsyncTask> tasks;

    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduled_stop_info);
        scheduledStop = StopTimesActivity.selectedStop;
        scheduledStop.loadAdditionalInfo();
        use24hrTime = ActivityUtilities.getTimeSetting(this);
        upcomingStopNumbers = new ArrayList<>();
        upcomingStops = new ArrayList<>();

        ListView listView = ((ListView)findViewById(R.id.listView_upcoming_stops));
        View headerView = getLayoutInflater().inflate(R.layout.listview_upcoming_stops_header, null);
        headerView.setEnabled(false);
        listView.addHeaderView(headerView);

        adapter = new UpcomingStopsAdapter(this, R.layout.upcoming_stops_row, upcomingStops, use24hrTime);
        listView.setAdapter(adapter);
        tasks = new ArrayList<>();


        fillTextViews();
        tasks.add(new LoadStopsForRoute().execute(BusUtilities.generateSearchQuery(scheduledStop.getRouteNumber()).getQueryUrl()));
    }

    public void onResume(){
        super.onResume();
        use24hrTime = ActivityUtilities.getTimeSetting(this);
        fillTextViews();
    }

    public void onDestroy() {
        super.onDestroy();
        for (AsyncTask task : tasks)
            task.cancel(true);
    }

    private void fillTextViews() {
        TextView routeNumber = getTextView(R.id.bus_number);
        routeNumber.setText(Integer.toString(scheduledStop.getRouteNumber()));
        ActivityUtilities.setTextViewColour(this, routeNumber, scheduledStop);

        getTextView(R.id.bus_name).setText(scheduledStop.getRouteVariantName());
        if(scheduledStop.hasArrivalTime()) {
            findViewById(R.id.arrival_times_header).setVisibility(View.VISIBLE);

            TextView arrival = getTextView(R.id.scheduled_arrival);
            arrival.setText(scheduledStop.getScheduledArrivalTime().toFormattedString(null, use24hrTime));
            arrival.setVisibility(View.VISIBLE);

            TextView departure = getTextView(R.id.estimated_arrival);
            departure.setText(scheduledStop.getEstimatedArrivalTime().toFormattedString(null, use24hrTime));
            departure.setVisibility(View.VISIBLE);
        }

        getTextView(R.id.scheduled_departure).setText(scheduledStop.getScheduledDepartureTime().toFormattedString(null, use24hrTime));
        getTextView(R.id.estimated_departure).setText(scheduledStop.getEstimatedDepartureTime().toFormattedString(null, use24hrTime));

        getTextView(R.id.has_bike_rack).setText("Bike rack: " + booleanStringValue(scheduledStop.hasBikeRack()));
        getTextView(R.id.has_easy_access).setText("Easy access: " + booleanStringValue(scheduledStop.hasEasyAccess()));
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                createNotification();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.route_number_background_rt)
                        .setContentTitle(Integer.toString(scheduledStop.getRouteNumber()) + " " + scheduledStop.getRouteVariantName())
                        .setContentText(scheduledStop.getEstimatedDepartureTime().toFormattedString(null, false))
                        .setAutoCancel(true);

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1337, mBuilder.build());

       callAsynchronousTask();
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            UpdateNotification updateNotification = new UpdateNotification();
                            updateNotification.execute(BusUtilities.generateStopNumberURL(10545, scheduledStop.getRouteNumber(), null, null));
                        } catch (Exception e) {
                            mBuilder.setContentText(e.getMessage());
                            Log.e("error", e.getMessage());
                            mNotifyMgr.notify(1337, mBuilder.build());
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);
    }

    public class LoadStopsForRoute extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                NodeList stops = ((Document)result.getResult()).getElementsByTagName(StopTimesNodeTags.STOP.tag);
                if(stops.getLength() > 0) {
                    for (int s = 0; s < stops.getLength(); s++) {
                        Node stop = stops.item(s);
                        int stopNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, stop));
                        upcomingStopNumbers.add(stopNumber);

                        try {
                            tasks.add(new LoadStopTimes().executeOnExecutor(THREAD_POOL_EXECUTOR, BusUtilities.generateStopNumberURL(stopNumber, scheduledStop.getRouteNumber(), scheduledStop.getEstimatedDepartureTime(), null)));
                        }catch (Exception e) {
                            Log.e("Task", "task error");
                        }

                    }

                }else {
                    ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.no_results_found).toString());
                }
            }else if(result.getException() != null) {
                ActivityUtilities.createLongToaster(getApplicationContext(), getText(R.string.network_error).toString());
            }
        }
    }

    public class LoadStopTimes extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {
                Stop stop = new Stop((Document) result.getResult());
                stop.loadRoutes();
                String stopName = stop.getName();
                ScheduledStop scheduledStop1 = stop.getScheduledStopByKey(scheduledStop.getKey());

                if(scheduledStop1 != null) {
                    UpcomingStop upcomingStop = new UpcomingStop(stopName, stop.getStopNumber(), scheduledStop1.getEstimatedDepartureTime(), scheduledStop1.getKey());
                    upcomingStops.add(upcomingStop);
                    Collections.sort(upcomingStops, new Comparator<UpcomingStop>() {
                        @Override
                        public int compare(UpcomingStop lhs, UpcomingStop rhs) {
                            return lhs.getKey().getStopNumber() - rhs.getKey().getStopNumber();
                        }
                    });
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class UpdateNotification extends LoadXMLAsyncTask {
        @Override
        protected void onPostExecute(LoadResult result) {
            if (result.getResult() != null) {

                    Stop stop = new Stop((Document)result.getResult());
                    stop.loadRoutes();
                    ScheduledStop scheduledStop1 = stop.getScheduledStopByKey(scheduledStop.getKey());

                if(scheduledStop1 != null) {
                    mBuilder.setContentText(scheduledStop1.getEstimatedDepartureTime().toFormattedString(new StopTime(new Date()), false) + " ver" + Integer.toString(i++));

                    mNotifyMgr.notify(1337, mBuilder.build());
                }
                else {
                    timer.cancel();
                    //mNotifyMgr.cancelAll();
                    Log.e("cancel", "Cancelling timer");
                }

            }
        }
    }
}
