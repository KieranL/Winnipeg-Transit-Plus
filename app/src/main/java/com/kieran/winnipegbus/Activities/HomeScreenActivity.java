package com.kieran.winnipegbus.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kieran.winnipegbus.ActivityUtilities;
import com.kieran.winnipegbus.AppRater;
import com.kieran.winnipegbus.R;


public class HomeScreenActivity extends BaseActivity {
    public static final String SEARCH_QUERY = "search_query";
    public final static String STOP_NUMBER = "stop_number";
    public static String filesDir;
    private Button searchButton;
    private EditText searchField;

    @Override
    protected void onRestart() {
        super.onRestart();

        updateGoButtonStatus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        adViewResId = R.id.homeScreenAdView;

        AppRater rater = new AppRater(this);
        rater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now_button, R.string.rate_later_button, R.string.rate_never_button);
        rater.show();

        if(filesDir == null)
            filesDir = getFilesDir().getPath();

        searchButton = (Button) findViewById(R.id.search_button);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    submitSearch(v);
                    return true;
                }
                return false;
            }
        });


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

        initializeAdsIfEnabled();
        searchButton.setEnabled(false);
        searchField.addTextChangedListener(watcher);

    }

    private void updateGoButtonStatus() {
        searchButton.setEnabled(searchField.getText().length() > 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favourites:
                super.openFavourites();
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
                startSearchResultsActivity();
            }
        }catch (Exception e) {
           startSearchResultsActivity();
        }
    }

    private void startSearchResultsActivity() {
        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra(SEARCH_QUERY, searchField.getText().toString());
        startActivity(intent);
    }
}
