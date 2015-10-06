package com.kieran.winnipegbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ActivityUtilities {
    public static void createLongToaster(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void openFavourites(Context context) {
        Intent intent = new Intent(context, FavouritesActivity.class);
        context.startActivity(intent);
    }

    public static void openStopTimes(Context context, int stopNumber) {
        Intent intent = new Intent(context, StopTimesActivity.class);

        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        context.startActivity(intent);
    }

    public static void openSettings(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static boolean adsDisabled(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_ads_disabled", false);
    }

    public static AdView createAd(Context context, AdView adView) {
        AdRequest.Builder adRequest = new AdRequest.Builder();
        adRequest.addTestDevice(context.getString(R.string.test_device_id_gs5));
        adView.loadAd(adRequest.build());

        return adView;
    }

    public static AdView initializeAdsIfEnabled(Context context, AdView adView) {
        if (!adView.isLoading() && !adsDisabled(context)) {
            adView.setVisibility(View.VISIBLE);
            adView = createAd(context, adView);
        } else {
            adView = destroyAdView(adView);
        }
        return adView;
    }

    public static AdView destroyAdView(AdView adView) {
        if(adView != null) {
            adView.setVisibility(View.GONE);
            adView.destroy();
        }
        return adView;
    }
}
