package com.kieran.winnipegbus.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.classes.TripParameters;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

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
            setTextViewText(R.id.origin_textView, tripParameters.getOrigin().getTitle());
        if (tripParameters.getDestination() != null)
            setTextViewText(R.id.destination_textView, tripParameters.getDestination().getTitle());
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

                setTextViewText(R.id.trip_time_date, tripParameters.getTime().toDateString());
            }
        }, tripParameters.getTime().getYear(), tripParameters.getTime().getMonth(), tripParameters.getTime().getDayOfMonth());
        d.setTitle("");
        d.show();
    }

    @Override
    public void OnReceive(LoadResult<JSONObject> result) {

    }
}
