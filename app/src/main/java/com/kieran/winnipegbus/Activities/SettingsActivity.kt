package com.kieran.winnipegbus.Activities

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.PreferenceManager
import android.support.v7.app.ActionBar

import com.kieran.winnipegbus.ActivityManager
import com.kieran.winnipegbus.R

class SettingsActivity : AppCompatPreferenceActivity() {

    val themeResId: Int
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val themeId = Integer.parseInt(prefs.getString(getString(R.string.pref_key_theme), "0"))
            when (themeId) {
                0 -> return R.style.Dark
                1 -> return R.style.Light
                2 -> return R.style.Rt
                else -> return R.style.Dark
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
        setTheme(themeResId)
    }

    override fun onApplyThemeResource(theme: Resources.Theme, resId: Int, first: Boolean) {
        theme.applyStyle(themeResId, true)
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        context = this

        setupSimplePreferencesScreen()
    }

    private fun setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.xml.pref_container)

        var fakeHeader = PreferenceCategory(this)
        fakeHeader.setTitle(R.string.pref_header_general)
        preferenceScreen.addPreference(fakeHeader)
        addPreferencesFromResource(R.xml.pref_general)

        fakeHeader = PreferenceCategory(this)
        fakeHeader.setTitle(R.string.pref_header_scheduled_stops)
        preferenceScreen.addPreference(fakeHeader)
        addPreferencesFromResource(R.xml.pref_scheduled_stops)

        fakeHeader = PreferenceCategory(this)
        fakeHeader.setTitle(R.string.pref_header_favourite_stops)
        preferenceScreen.addPreference(fakeHeader)
        addPreferencesFromResource(R.xml.pref_favourite_stops)

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_theme)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_nearby_distance)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_favourites_sort)))
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_schedule_load_interval)))
    }

    companion object {
        private var context: Context? = null

        val themeId: Int
            get() {
                val prefs = PreferenceManager.getDefaultSharedPreferences(context)
                return Integer.parseInt(prefs.getString(context!!.getString(R.string.pref_key_theme), "0"))
            }

        private val sBindPreferenceSummaryToValueListener = Preference.OnPreferenceChangeListener { preference, value ->
            val stringValue = value.toString()

            if (preference is ListPreference) {
                val index = preference.findIndexOfValue(stringValue)

                preference.setSummary(if (index >= 0) preference.entries[index] else null)
            } else {
                preference.summary = stringValue
            }

            if (preference.key == "pref_theme") {
                val newThemeId = Integer.parseInt(value as String)
                if (newThemeId != themeId) {
                    ActivityManager.refreshThemes()
                    setTheme()
                }

            }

            true
        }

        private fun setTheme() {
            (context as Activity).recreate()
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, ""))
        }
    }
}
