package com.kieran.winnipegbus.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.ActivityManager;
import com.kieran.winnipegbus.R;

public abstract class BaseActivity extends AppCompatActivity {
    private AdView adView;
    protected int adViewResId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeResId());
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
    }

    protected void onRestart() {
        super.onRestart();
        initializeAdsIfEnabled();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adView != null)
            adView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyAdView();
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

    public void initializeAdsIfEnabled() {
        if(adView == null && adViewResId != 0)
            adView = (AdView) findViewById(adViewResId);

        if (adView != null && !adView.isLoading() && !adsDisabled()) {
            adView.setVisibility(View.VISIBLE);
            createAd();
        } else {
            destroyAdView();
        }
    }

    public void createAd() {
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(getString(R.string.test_device_id_gs5));
        adView.loadAd(adRequest.build());
    }

    public void destroyAdView() {
        if(adView != null) {
            adView.setVisibility(View.GONE);
            adView.destroy();
        }
    }

    public boolean adsDisabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        return prefs.getBoolean("pref_ads_disabled", false);
    }

    public int getThemeResId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int themeId = Integer.parseInt(prefs.getString(getString(R.string.pref_key_theme), "0"));
        switch (themeId) {
            case 0: return R.style.Dark;
            case 1: return R.style.Light;
            case 2: return R.style.Rt;
            default: return R.style.Dark;
        }
    }
}
