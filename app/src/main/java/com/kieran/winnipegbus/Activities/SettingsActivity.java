package com.kieran.winnipegbus.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;

import com.kieran.winnipegbus.ActivityManager;
import com.kieran.winnipegbus.R;

public class SettingsActivity extends PreferenceActivity {
    private static Context context;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        context = this;

        setupSimplePreferencesScreen();
    }

    public static int getThemeId() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(context.getString(R.string.pref_key_theme), "0"));
    }

    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_container);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_general);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_general);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_scheduled_stops);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_scheduled_stops);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.pref_header_favourite_stops);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_favourite_stops);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_theme)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_favourites_sort)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_schedule_load_interval)));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else {
                preference.setSummary(stringValue);
            }

            if(preference.getKey().equals("pref_theme")) {
                int newThemeId  = Integer.parseInt((String)value);
                if(newThemeId != getThemeId()) {
                    ActivityManager.refreshThemes();
                }

            }

            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }
}
