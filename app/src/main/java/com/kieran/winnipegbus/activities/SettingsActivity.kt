package com.kieran.winnipegbus.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.preference.*

import com.kieran.winnipegbus.ActivityManager
import com.kieran.winnipegbus.R

class SettingsActivity : AppCompatPreferenceActivity() {

    val themeResId: Int
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val themeId = prefs.getString(getString(R.string.pref_key_theme), "0")?.toInt()
            return when (themeId) {
                0 -> R.style.Dark
                1 -> R.style.Light
                2 -> R.style.Rt
                else -> R.style.Dark
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

    @SuppressLint("ApplySharedPref")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        context = this

        setupSimplePreferencesScreen()


        val pref = findPreference("pref_disable_ads") as CheckBoxPreference
        pref.setOnPreferenceChangeListener { preference, _ ->
            if (!(preference as CheckBoxPreference).isChecked) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(R.string.disable_ads_confirmation_title)
                builder.setMessage(R.string.disable_ads_confirmation)
                builder.setPositiveButton(R.string.yes, { _: DialogInterface, _: Int ->
                    preference.isChecked = true
                })
                builder.setNegativeButton(R.string.no, null)

                builder.show()

                false
            } else {
                true
            }
        }
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
                return prefs.getString(context!!.getString(R.string.pref_key_theme), "0")?.toInt()!!
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
