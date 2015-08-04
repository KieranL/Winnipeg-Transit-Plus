package com.kieran.winnipegbus;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Document;

import java.util.List;

import winnipegbusbackend.BusUtilities;
import winnipegbusbackend.ScheduledStop;
import winnipegbusbackend.Stop;


public class StopTimesActivity extends AppCompatActivity {

    private BusUtilities utilities = new BusUtilities();
    private TextView routeNumber;
    private TextView routeName;
    private TextView timeStatus;
    private TextView departureTime;
    private String urlPath;
    private int stopNumber;
    private static Menu menu;
    boolean loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);

        findTextViews();
        getTimes();
    }

    private void findTextViews() {
        routeNumber = (TextView) findViewById(R.id.route_number_text);
        routeName = (TextView) findViewById(R.id.route_name_text);
        timeStatus = (TextView) findViewById(R.id.time_status_text);
        departureTime = (TextView) findViewById(R.id.departure_time_text);
    }

    private void getTimes() {
        loading = true;


        Intent intent = getIntent();
        stopNumber = intent.getIntExtra(HomeScreenActivity.STOP_NUMBER, 0);
        setTitle(Integer.toString(stopNumber));
        int[] routeNumbers;
        try {
            String[] routeNumberString = intent.getStringExtra(HomeScreenActivity.ROUTE_NUMBER).split(" ");
            routeNumbers = new int[routeNumberString.length];

            for (int i = 0; i < routeNumberString.length; i++)
                routeNumbers[i] = Integer.parseInt(routeNumberString[i]);
        } catch (Exception e) {
            routeNumbers = new int[]{0};
        }

        try {
            urlPath = utilities.generateURL("stops/" + stopNumber + "/schedule?", routeNumbers);

            loadTimes();
        } catch (Exception e) {
            routeName.setText(this.getResources().getString(R.string.enter_valid_stop_number));
            setTitle(this.getResources().getString(R.string.app_name));
        }
    }

    private void loadTimes() {
        clearTextViews();
        new LoadStopTimes().execute(urlPath);

    }

    private void finishLoading() {
            MenuItem m = menu.findItem(R.id.refresh_button);
            if(m.getActionView()!=null)
            {
                // Remove the animation.
                m.getActionView().clearAnimation();
                m.setActionView(null);
            }

        loading = false;
    }

    private void clearTextViews() {
        routeNumber.setText("");
        routeName.setText("");
        timeStatus.setText("");
        departureTime.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);
        this.menu = menu;

        refresh(menu.findItem(R.id.refresh_button));

        if(FavouritesActivity.favouritesList.contains(stopNumber))
            menu.findItem(R.id.add_to_favourites_button).setIcon(R.drawable.ic_favorite_stops);
        return true;
    }

    private void refresh(MenuItem item) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);
        item.setActionView(iv);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_button) {
            refresh(item);
            loadTimes();
            return true;
        } else if (id == R.id.add_to_favourites_button) {
            if(FavouritesActivity.favouritesList.contains(stopNumber)) {
                FavouritesActivity.removeFromFavourites(stopNumber);
                item.setIcon(R.drawable.ic_add_to_favourites);
            }else{
                FavouritesActivity.addToFavourites(stopNumber);
                item.setIcon(R.drawable.ic_favorite_stops);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String stringArrayToStringLines(String[] lines) {
        String combinedString = "";

        for (int i = 0; i < lines.length; i++) {
            combinedString += lines[i];

            if (i < lines.length - 1)
                combinedString += "\n";
        }

        return combinedString;
    }

    private class LoadStopTimes extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            return utilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(Document result) {
            String[] routeNumbers;
            String[] routeNames;
            String[] timeStatuses;
            String[] departureTimes;

            if (result != null) {
                Stop stop = new Stop(result);
                setTitle(stop.getName());
                List<ScheduledStop> stops = stop.getScheduledStopsSorted();

                routeNumbers = new String[stops.size()];
                routeNames = new String[stops.size()];
                timeStatuses = new String[stops.size()];
                departureTimes = new String[stops.size()];

                for (int i = 0; i < stops.size(); i++) {
                    routeNumbers[i] = Integer.toString(stops.get(i).getRouteNumber());
                    routeNames[i] = stops.get(i).getRouteVariantName();
                    timeStatuses[i] = stops.get(i).getTimeStatus();
                    departureTimes[i] = stops.get(i).toString();
                }

                routeNumber.setText(stringArrayToStringLines(routeNumbers));
                routeName.setText(stringArrayToStringLines(routeNames));
                timeStatus.setText(stringArrayToStringLines(timeStatuses));
                departureTime.setText(stringArrayToStringLines(departureTimes));
            } else {
                routeName.setText(urlPath);
            }
            finishLoading();
        }

        private void finishLoading() {
            MenuItem m = menu.findItem(R.id.refresh_button);
            if(m.getActionView()!=null)
            {
                // Remove the animation.
                m.getActionView().clearAnimation();
                m.setActionView(null);
            }

            loading = false;
        }

    }

}