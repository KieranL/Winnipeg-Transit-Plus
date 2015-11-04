package com.kieran.winnipegbus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeResId());
        super.onCreate(savedInstanceState);
    }

    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openFavourites() {
        Intent intent = new Intent(this, FavouritesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public int getThemeResId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int themeId = Integer.parseInt(prefs.getString("pref_theme", "0"));
        switch (themeId) {
            case 0: return R.style.Dark;
            case 1: return R.style.Light;
            default: return R.style.Dark;
        }
    }
}
