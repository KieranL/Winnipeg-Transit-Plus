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

import com.google.android.gms.ads.AdView;


public class HomeScreenActivity extends AppCompatActivity {
    public static final String SEARCH_QUERY = "search_query";
    public final static String STOP_NUMBER = "stop_number";
    public static String filesDir;
    private Button button;
    private EditText searchField;
    private AdView adView;

    @Override
    protected void onRestart() {
        super.onRestart();

        updateGoButtonStatus();
        adView = ActivityUtilities.initializeAdsIfEnabled(this, adView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        filesDir = getFilesDir().getPath();

        adView = (AdView) findViewById(R.id.homeScreenAdView);
        button = (Button) findViewById(R.id.button);

        searchField = (EditText) findViewById(R.id.search_field);


        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateGoButtonStatus();
            }
        };

        adView = ActivityUtilities.initializeAdsIfEnabled(this, adView);
        button.setEnabled(false);
        searchField.addTextChangedListener(watcher);

    }

    @Override
    public void onPause() {
        super.onPause();
        adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        adView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adView = ActivityUtilities.destroyAdView(adView);
    }

    private void updateGoButtonStatus() {


        button.setEnabled(searchField.getText().length() > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                ActivityUtilities.openSettings(this);
                return true;
            case R.id.favourites:
                ActivityUtilities.openFavourites(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void submitSearch(View view) {
        try{
            int number = Integer.parseInt(searchField.getText().toString().trim());

            if(number >= 10000) {
                ActivityUtilities.openStopTimes(this, number);
            }else {
                startSearchActivity();
            }
        }catch (Exception e) {
           startSearchActivity();
        }
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SEARCH_QUERY, searchField.getText().toString());
        startActivity(intent);
    }
}
