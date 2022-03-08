package com.kieran.winnipegbus.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.kieran.winnipegbus.ActivityManager
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.data.SQLiteFavouritesRepository
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.exceptions.RateLimitedException
import com.kieran.winnipegbusbackend.exceptions.TransitDataNotFoundException
import com.kieran.winnipegbusbackend.favourites.FavouritesService
import com.kieran.winnipegbusbackend.interfaces.Logger
import com.kieran.winnipegbusbackend.interfaces.TransitService

import java.io.IOException

abstract class BaseActivity : AppCompatActivity() {
    private var adView: AdView? = null
    protected var adViewResId = 0

    protected val context: Context
        get() = this

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

    protected val timeSetting: Boolean
        get() = getTimeSetting(this)

    val isOnline: Boolean
        get() {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

    val isLocationEnabled: Boolean
        get() {
            var locationMode = 0
            val locationProviders: String
            val isLocationEnabled: Boolean

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)

                } catch (ex: Settings.SettingNotFoundException) {
                    Logger.getLogger().error(ex, "Error getting location setting")
                }

                isLocationEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF

            } else {
                locationProviders = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
                isLocationEnabled = !TextUtils.isEmpty(locationProviders)
            }
            return isLocationEnabled && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

    val sortPreference: FavouritesListSortType
        get() {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            return FavouritesListSortType.getEnum(prefs.getString(getString(R.string.pref_favourites_sort), "0"))
        }

    protected val transitService: TransitService by lazy {
        TransitServiceProvider.getTransitService()
    }

    protected val favouritesService: FavouritesService by lazy {
        getFavouritesService(transitService.getAgencyId())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(themeResId)
        if (filesDirPath == null)
            filesDirPath = filesDir.path
        ActivityManager.addActivity(this)
        super.onCreate(savedInstanceState)
    }

    override fun onRestart() {
        super.onRestart()
        initializeAdsIfEnabled()
    }

    override fun onPause() {
        super.onPause()
        if (adView != null)
            adView!!.pause()
    }

    override fun onResume() {
        super.onResume()
        if (adView != null)
            adView!!.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyAdView()
        ActivityManager.removeActivity(this)
    }

    fun openSettings() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun openFavourites() {
        val intent = Intent(this, FavouritesActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                openSettings()
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun initializeAdsIfEnabled() {
        if (adView == null && adViewResId != 0)
            adView = findViewById<View>(adViewResId) as AdView

        if (adView != null && !adView!!.isLoading && !adsDisabled()) {
            adView!!.visibility = View.VISIBLE
            createAd()
        } else {
            destroyAdView()
        }
    }

    private fun createAd() {
        try {
            val adRequest = AdRequest.Builder()
            adView!!.loadAd(adRequest.build())
        } catch (ex: Exception) {
            Logger.getLogger().error(ex, "Error Initializing adview")
        }
    }

    private fun destroyAdView() {
        adView?.visibility = View.GONE
        adView?.destroy()
    }

    fun isBooleanSettingEnabled(key: String, defaultValue: Boolean = false): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean(key, defaultValue)
    }

    private fun adsDisabled(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs.getBoolean("pref_disable_ads", false)
    }

    fun openStopTimes(stop: FavouriteStop) {
        val intent = Intent(this, StopTimesActivity::class.java)
        intent.putExtra(StopTimesActivity.STOP, stop)
        startActivity(intent)
    }

    fun showLongToaster(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    protected fun showLongToaster(@StringRes resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_LONG).show()
    }

    fun showShortToaster(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    protected fun showShortToaster(@StringRes resId: Int) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
    }

    protected fun setTextViewText(id: Int, text: String) {
        val view = findViewById<View>(id)

        if (view != null)
            (view as TextView).text = text
    }

    protected fun handleException(ex: Exception) {
        val resId: Int = when (ex) {
            is RateLimitedException -> R.string.too_many_queries_error
            is IOException -> R.string.network_error
            is TransitDataNotFoundException -> R.string.data_not_found
            else -> R.string.unknown_error
        }

        showShortToaster(resId)
    }

    fun getFavouritesService(agencyId: Long): FavouritesService {
        val favouritesRepository = SQLiteFavouritesRepository.getInstance(this)
        return FavouritesService.getInstance(favouritesRepository, agencyId)
    }

    companion object {
        var filesDirPath: String? = null

        fun getTimeSetting(context: Context): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            return prefs.getBoolean("pref_use_24hr_time", false)
        }
    }
}
