package com.kieran.winnipegbus.Activities;

import android.os.Bundle;
import android.view.Menu;

import com.kieran.winnipegbus.R;

public class TripPlannerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_planner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_trip_planner, menu);
        return true;
    }

}
