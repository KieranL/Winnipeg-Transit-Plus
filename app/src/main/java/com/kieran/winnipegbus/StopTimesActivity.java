package com.kieran.winnipegbus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

import winnipegbusbackend.BusUtilities;
import winnipegbusbackend.FavouriteStop;
import winnipegbusbackend.FavouriteStopsList;
import winnipegbusbackend.ScheduledStopInfo;
import winnipegbusbackend.Stop;


public class StopTimesActivity extends AppCompatActivity {

    private BusUtilities utilities = new BusUtilities();
    private String urlPath;
    private int stopNumber;
    private String stopName;
    private Menu menu;
    private Stop stop;
    boolean loading;
    List<ScheduledStopInfo> stops = new ArrayList<>();
    static ListView listView;
    StopTimeAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_times);
        FavouriteStopsList.loadFavourites();

        listView = (ListView) findViewById(R.id.stop_times_listview);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new StopTimeAdapter(this, R.layout.listview_stop_times_row, stops);

        listView.setAdapter(adapter);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_stop_times_header, null));
        getTimes();
    }


    private void getTimes() {
        loading = true;

        Intent intent = getIntent();
        stopNumber = intent.getIntExtra(HomeScreenActivity.STOP_NUMBER, 0);
        setTitle("Stop " + Integer.toString(stopNumber));
        int[] routeNumbers;

        try {
            String[] routeNumberString = intent.getStringExtra(HomeScreenActivity.ROUTE_NUMBER).split(" ");
            routeNumbers = new int[routeNumberString.length];

            for (int i = 0; i < routeNumberString.length; i++)
                routeNumbers[i] = Integer.parseInt(routeNumberString[i]);
        } catch (Exception e) {
            routeNumbers = new int[]{0};
        }


            urlPath = utilities.generateURL("stops/" + stopNumber + "/schedule?", routeNumbers);

            loadTimes();

    }

    private void loadTimes() {
        new LoadStopTimes().execute(urlPath);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_stop_times, menu);
        this.menu = menu;

        refresh(menu.findItem(R.id.refresh_button));

        if(FavouriteStopsList.contains(stopNumber))
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.refresh_button:
                refresh(item);
                loadTimes();
                return true;
            case R.id.add_to_favourites_button:
                if(FavouriteStopsList.contains(stopNumber) && !loading) {

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
                }else if (!loading){
                    FavouriteStopsList.addToFavourites(new FavouriteStop(stopName, stopNumber));
                    item.setIcon(R.drawable.ic_favorite_stops);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class LoadStopTimes extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            return utilities.getXML(urls[0]);
        }

        @Override
        protected void onPostExecute(Document result) {

            if (result != null) {
                stop = new Stop(result, stopNumber);
                stopName = stop.getName();
                stops.clear();
                stops.addAll(stop.getScheduledStopInfosSorted());

            } else {
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
            finishLoading();
        }

        private void finishLoading() {
            MenuItem m = menu.findItem(R.id.refresh_button);
            if(m.getActionView() != null) {
                m.getActionView().clearAnimation();
                m.setActionView(null);
            }

            ((TextView)findViewById(R.id.listview_stop_times_header_text)).setText(stopName);

            adapter.notifyDataSetChanged();
            loading = false;
        }

    }

}