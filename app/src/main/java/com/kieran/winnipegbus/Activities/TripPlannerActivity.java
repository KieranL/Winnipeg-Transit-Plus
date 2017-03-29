package com.kieran.winnipegbus.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.kieran.winnipegbus.Adapters.TripPlannerAdapter;
import com.kieran.winnipegbus.LocationPickerDialog;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.TimeMode;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip;
import com.kieran.winnipegbusbackend.TripPlanner.classes.TripParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class TripPlannerActivity extends GoogleApiActivity implements TransitApiManager.OnJsonLoadResultReceiveListener {
    private static final String PARAMETERS = "parameters";
    private TripParameters tripParameters = new TripParameters();
    private List<Trip> trips;
    private TripPlannerAdapter adapter;
    private Button getDirectionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_planner);
        Intent intent = getIntent();

        tripParameters = (TripParameters) intent.getSerializableExtra(PARAMETERS);

        if(tripParameters == null)
            tripParameters = new TripParameters();

        trips = new ArrayList<>();
        ExpandableListView listView = (ExpandableListView) findViewById(R.id.trip_planner_listview);
        listView.addHeaderView(getLayoutInflater().inflate(R.layout.listview_trip_planner_header, null));
        adapter = new TripPlannerAdapter(this, trips);
        listView.setAdapter(adapter);

        getDirectionsButton = (Button) findViewById(R.id.get_directions_button);

        initializeFields();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        connectClient();
    }

    private void initializeFields() {
        if (tripParameters.getOrigin() != null)
            ((Button)findViewById(R.id.origin_select_button)).setText(tripParameters.getOrigin().getTitle());
        if (tripParameters.getDestination() != null)
            ((Button)findViewById(R.id.destination_select_button)).setText(tripParameters.getDestination().getTitle());

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

        getDirectionsButton.setEnabled(tripParameters.isValid());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_planner, menu);
        return true;
    }

    public void getDirections(View view) {
        trips.clear();
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



            for(int i = 0; i < plans.length(); i++) {
                trips.add(new Trip(plans.getJSONObject(i)));
            }

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocation();
    }

    public void selectOrigin(View view) {
        new LocationPickerDialog(this, new LocationPickerDialog.OnLocationPickedListener() {
            @Override
            public void OnLocationPickedListener(Location location) {
                tripParameters.setOrigin(location);
                initializeFields();
            }
        }).show();
    }

    public void selectDestination(View view) {
        new LocationPickerDialog(this, new LocationPickerDialog.OnLocationPickedListener() {
            @Override
            public void OnLocationPickedListener(Location location) {
                tripParameters.setDestination(location);
                initializeFields();
            }
        }).show();
    }

    public void swapLocations(View view) {
        tripParameters.swapLocations();
        initializeFields();
    }
}
