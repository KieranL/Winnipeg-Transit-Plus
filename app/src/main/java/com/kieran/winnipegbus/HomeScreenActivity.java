package com.kieran.winnipegbus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class HomeScreenActivity extends AppCompatActivity {
    public static String filesDir;
    public final static String STOP_NUMBER = "stop_number";
    public final static String ROUTE_NUMBER = "route_number";
    Button button;
    EditText stopNumberField;
    EditText routeNumberField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        filesDir = getFilesDir().getPath();
        setContentView(R.layout.activity_home_screen);
        button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        stopNumberField = (EditText) findViewById(R.id.stop_number_field);
        routeNumberField = (EditText) findViewById(R.id.route_number_field);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {  }

            @Override
            public void afterTextChanged(Editable s) {
                updateGoButtonStatus();
            }
        };

        stopNumberField.addTextChangedListener(watcher);
    }

    private void updateGoButtonStatus() {
        button.setEnabled(stopNumberField.getText().length() == 5);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.settings:
                openSettings();
                return true;
            case R.id.favourites:
                openFavourites();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void getStopTimes(View view) {
        Intent intent = new Intent(this, StopTimesActivity.class);

        int stopNumber = Integer.parseInt(stopNumberField.getText().toString());
        String routeNumber = routeNumberField.getText().toString();
        intent.putExtra(STOP_NUMBER, stopNumber);
        intent.putExtra(ROUTE_NUMBER, routeNumber);
        startActivity(intent);
    }

    public void openFavourites() {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }
}
