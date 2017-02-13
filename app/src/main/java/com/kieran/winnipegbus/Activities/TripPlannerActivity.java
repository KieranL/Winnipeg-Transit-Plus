package com.kieran.winnipegbus.Activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory;
import com.kieran.winnipegbusbackend.TripPlanner.TimeMode;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.TripParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TripPlannerActivity extends BaseActivity implements TransitApiManager.OnJsonLoadResultReceiveListener {
    private static final String PARAMETERS = "parameters";
    private TripParameters tripParameters = new TripParameters();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_planner);
        Intent intent = getIntent();

        tripParameters = (TripParameters) intent.getSerializableExtra(PARAMETERS);

        if(tripParameters == null)
            tripParameters = new TripParameters();

        initializeFields();
    }

    private void initializeFields() {
        if (tripParameters.getOrigin() != null)
            ((SearchView)findViewById(R.id.origin_searchView)).setQuery(tripParameters.getOrigin().getTitle(), false);
        if (tripParameters.getDestination() != null)
            ((SearchView)findViewById(R.id.destination_searchView)).setQuery(tripParameters.getDestination().getTitle(), false);

        setTextViewText(R.id.trip_time_hour_minute, tripParameters.getTime().toFormattedString(null, getTimeSetting()));
        setTextViewText(R.id.trip_time_date, tripParameters.getTime().toDatePickerDateFormat());

        Spinner timeModeView = (Spinner) findViewById(R.id.time_mode_spinner);
        final Adapter adapter = timeModeView.getAdapter();

        for(int i = 0; i < adapter.getCount(); i++) {
            if(((String)adapter.getItem(i)).equals(tripParameters.getTimeMode().name)) {
                timeModeView.setSelection(i);
            }
        }

        timeModeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tripParameters.setTimeMode(TimeMode.getTimeModeByName((String) adapter.getItem(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final Context self = this;
        SearchView originSearchView = (SearchView) findViewById(R.id.origin_searchView);
        originSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = TransitApiManager.generateLocationQueryUrl(query);
                TransitApiManager.getJsonAsync(url, new TransitApiManager.OnJsonLoadResultReceiveListener() {
                    @Override
                    public void OnReceive(LoadResult<JSONObject> result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(self);

                        try {
                            JSONArray locationNodes = result.getResult().getJSONArray("locations");
                            final List<Location> locations = new ArrayList<>();
                            for(int i = 0; i < locationNodes.length(); i++) {
                                locations.add(LocationFactory.createLocation(locationNodes.getJSONObject(i)));
                            }


                            CharSequence charSequence[] = new CharSequence[locations.size()];

                            for (int i = 0; i < charSequence.length; i++)
                                charSequence[i] = locations.get(i).getTitle();

                            builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tripParameters.setOrigin(locations.get(which));
                                    initializeFields();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        builder.create().show();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
            SearchView destinationSearchView = (SearchView) findViewById(R.id.destination_searchView);
            destinationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String url = TransitApiManager.generateLocationQueryUrl(query);
                    TransitApiManager.getJsonAsync(url, new TransitApiManager.OnJsonLoadResultReceiveListener() {
                        @Override
                        public void OnReceive(LoadResult<JSONObject> result) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(self);

                            try {
                                JSONArray locationNodes = result.getResult().getJSONArray("locations");
                                final List<Location> locations = new ArrayList<>();
                                for(int i = 0; i < locationNodes.length(); i++) {
                                    locations.add(LocationFactory.createLocation(locationNodes.getJSONObject(i)));
                                }


                                CharSequence charSequence[] = new CharSequence[locations.size()];

                                for (int i = 0; i < charSequence.length; i++)
                                    charSequence[i] = locations.get(i).getTitle();

                                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        tripParameters.setDestination(locations.get(which));
                                        initializeFields();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            builder.create().show();
                        }
                    });

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_planner, menu);
        return true;
    }

    public void getDirections(View view) {
        TransitApiManager.getJsonAsync(tripParameters.getURL(), this);
    }

    public void pickTime(View view) {
        final Calendar c = new GregorianCalendar();
        TimePickerDialog d = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c.setTimeInMillis(tripParameters.getTime().getMilliseconds());
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                tripParameters.setTime(new StopTime(c.getTimeInMillis()));

                setTextViewText(R.id.trip_time_hour_minute, tripParameters.getTime().toFormattedString(null, getTimeSetting()));
            }
        }, tripParameters.getTime().getHours(), tripParameters.getTime().getMinutes(), getTimeSetting());

        d.setTitle("");
        d.show();
    }

    public void pickDate(View view) {
        final Calendar c = new GregorianCalendar();
        final DatePickerDialog d = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.setTimeInMillis(tripParameters.getTime().getMilliseconds());
                c.set(Calendar.YEAR, year);
                c.set(Calendar.MONTH, monthOfYear);
                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tripParameters.setTime(new StopTime(c.getTimeInMillis()));

                setTextViewText(R.id.trip_time_date, tripParameters.getTime().toDatePickerDateFormat());
            }
        }, tripParameters.getTime().getYear(), tripParameters.getTime().getMonth(), tripParameters.getTime().getDayOfMonth());

        d.setTitle("");
        d.show();
    }

    @Override
    public void OnReceive(LoadResult<JSONObject> result) {
        JSONObject jsonObject = result.getResult();
        try {
            JSONArray plans = jsonObject.getJSONArray("plans");
            plans.length();
            List<String> strings = new ArrayList<>();

            for(int i = 0; i < plans.length(); i++) {
                strings.add(plans.getJSONObject(i).toString());
            }
            ListView view = (ListView) findViewById(R.id.trip_data);
            view.setAdapter(new ArrayAdapter<>(this, R.layout.simplerow, strings));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
