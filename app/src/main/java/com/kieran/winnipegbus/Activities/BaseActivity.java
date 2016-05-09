package com.kieran.winnipegbus.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.kieran.winnipegbus.ActivityManager;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.Stop;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

public abstract class BaseActivity extends AppCompatActivity {
    public static String filesDir;
    private AdView adView; //Static?
    protected int adViewResId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeResId());
        if(filesDir == null)
            filesDir = getFilesDir().getPath();
        ActivityManager.addActivity(this);
        super.onCreate(savedInstanceState);
    }

    protected void onRestart() {
        super.onRestart();
        initializeAdsIfEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adView != null)
            adView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyAdView();
        ActivityManager.removeActivity(this);
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
        adRequest.addTestDevice(getString(R.string.test_device_id_gs6e));
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

    protected Context getContext() {
        return this;
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

    protected boolean getTimeSetting() {
        return getTimeSetting(this);
    }

    protected void openStopTimes(Stop stop) {
        Intent intent = new Intent(this, StopTimesActivity.class);
        intent.putExtra(StopTimesActivity.STOP, stop);
        startActivity(intent);
    }

    protected void showLongToaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    protected void showShortToaster(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean getTimeSetting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_use_24hr_time", false);
    }

    public static void setTextViewColour(Context context, TextView textView, ScheduledStop scheduledStop) {
        setTextViewColour(context, textView, scheduledStop.getCoverageType(), scheduledStop.getRouteNumber());
    }

    @SuppressWarnings("deprecation")
    public static void setTextViewColour(Context context, TextView textView, CoverageTypes coverageType, int routeNumber) {
        if(BusUtilities.isDownTownExpress(routeNumber)) {
            textView.setTextColor(context.getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.route_number_background_dt_spirit);
        }else if(coverageType == CoverageTypes.REGULAR) {
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.route_number_background_regular);
        } else if(coverageType == CoverageTypes.EXPRESS || coverageType == CoverageTypes.SUPER_EXPRESS) {
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.route_number_background_express);
        } else if (coverageType == CoverageTypes.RAPID_TRANSIT) {
            textView.setTextColor(context.getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.route_number_background_rt);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;
        boolean isLocationEnabled;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isLocationEnabled =  locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isLocationEnabled =  !TextUtils.isEmpty(locationProviders);
        }
        return isLocationEnabled && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}
