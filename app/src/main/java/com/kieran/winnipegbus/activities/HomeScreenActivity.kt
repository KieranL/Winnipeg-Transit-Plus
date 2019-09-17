package com.kieran.winnipegbus.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouriteStopsList
import com.kieran.winnipegbusbackend.common.SearchQuery
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.enums.SearchQueryType
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.interfaces.TransitService
import com.rollbar.android.Rollbar
import com.rollbar.notifier.config.ConfigBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeScreenActivity : GoogleApiActivity(), LocationListener {
    private var searchButton: Button? = null
    private var searchField: EditText? = null
    private var favouritesFragment: FavouritesFragment? = null

    private val isSearchEnabled: Boolean
        get() = searchField!!.text.isNotEmpty()

    override fun onRestart() {
        super.onRestart()

        updateGoButtonStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        adViewResId = R.id.homeScreenAdView

        searchButton = findViewById<View>(R.id.search_button) as Button

        searchField = findViewById<View>(R.id.search_field) as EditText

        searchField!!.setOnKeyListener { v, keyCode, event ->
            if (isSearchEnabled && keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                submitSearch(v)
            }
            false
        }

        initializeAdsIfEnabled()
        searchButton!!.isEnabled = false
        searchField!!.addTextChangedListener(createTextWatcher())

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build()
        connectClient()

        Rollbar.init(this, null, null, false, true)

    }

    override fun onResume() {
        super.onResume()

        setupFrequentFavourites()
    }

    private fun setupFrequentFavourites(overrideCheck: Boolean = false) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FavouriteStopsList.isLoadNeeded = true
                val stops = favouritesService.getAll(FavouritesListSortType.FREQUENCY_DESC)

                if(stops.isNotEmpty()) {
                    runOnUiThread {
                        initializeFrequentFavourites(overrideCheck)
                    }
                }
            }catch (ex: Exception){
                Rollbar.instance()?.error(ex)
            }
        }
    }

    private fun initializeFrequentFavourites(overrideCheck: Boolean) {
        try {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this)

            if (!overrideCheck && !prefs.contains(stops_on_home_message_shown_flag)) {
                val alertDialog = android.app.AlertDialog.Builder(this)
                alertDialog.setTitle(R.string.enable_stops_on_home_prompt_title)
                alertDialog.setMessage(R.string.enable_stops_on_home_prompt)

                alertDialog.setPositiveButton(R.string.enable) { dialog, _ ->
                    val editor = prefs.edit()

                    editor.putBoolean(stops_on_home_message_shown_flag, true)
                    editor.putBoolean(prefFavouritesOnHome, true)

                    editor.apply()
                    setupFrequentFavourites()
                    dialog.dismiss()
                }

                alertDialog.setNegativeButton(R.string.disable) { dialog, _ ->
                    prefs.edit().putBoolean(stops_on_home_message_shown_flag, true).apply()
                    dialog.dismiss()
                }

                alertDialog.create().show()
            }

            if (isBooleanSettingEnabled(prefFavouritesOnHome) || overrideCheck) {
                if (favouritesFragment == null) {
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    favouritesFragment = FavouritesFragment()
                    fragmentTransaction.add(R.id.fragment_container, favouritesFragment).commit()
                }
            } else if (favouritesFragment != null) {
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.remove(favouritesFragment).commit()
                favouritesFragment = null
            }
        } catch (ex: Exception) {
            Rollbar.instance()?.error(ex)
        }
    }

    private fun createTextWatcher(): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                updateGoButtonStatus()
            }
        }
    }

    private fun updateGoButtonStatus() {
        searchButton!!.isEnabled = isSearchEnabled
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)

        val features = transitService.supportedFeatures()

        if (features.contains(SupportedFeature.TRIP_PLANNING)) {
            menu.findItem(R.id.trip_planner).isVisible = true
        }

        if (features.contains(SupportedFeature.SERVICE_ADVISORIES)) {
            menu.findItem(R.id.service_advisories).isVisible = true
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favourites -> {
                super.openFavourites()
                return true
            }
            R.id.nearby_stops -> {
                startNearbyStopsActivity()
                return true
            }
            R.id.service_advisories -> {
                startServiceAdvisoriesActivity()
                return true
            }
            R.id.trip_planner -> {
                startTripPlannerActivity()
                return true
            }
            R.id.action_privacy_policy -> {
                val alert = AlertDialog.Builder(this)
                alert.setTitle(R.string.privacy_policy_modal_title)

                val wv = WebView(this)
                wv.loadUrl(getString(R.string.privacy_url))
                wv.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)

                        return true
                    }
                }

                alert.setView(wv)
                alert.setNegativeButton(R.string.close) { dialog, _ -> dialog.dismiss() }
                alert.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun startTripPlannerActivity() {
        val intent = Intent(this, TripPlannerActivity::class.java)
        startActivity(intent)
    }

    private fun startServiceAdvisoriesActivity() {
        val intent = Intent(this, ServiceAdvisoriesActivity::class.java)
        startActivity(intent)
    }

    fun submitSearch(view: View) {
        val searchText = searchField!!.text.toString().trim { it <= ' ' }

        when (transitService.getSearchQueryType(searchText)) {
            SearchQueryType.STOP -> {
                openStopTimes(Stop(AgencySpecificClassFactory.createStopIdentifier(transitService.getAgencyId(), searchText)!!))
            }
            SearchQueryType.ROUTE_NUMBER -> {
                val intent = Intent(this, SearchResultsActivity::class.java)
                intent.putExtra(SearchResultsActivity.SEARCH_QUERY, SearchQuery(searchText, SearchQueryType.ROUTE_NUMBER))
                startActivity(intent)
            }
            SearchQueryType.GENERAL -> {
                startSearchResultsActivity(searchText)
            }
            else -> {
                showLongToaster(R.string.no_results_found)
            }
        }
    }

    private fun startSearchResultsActivity(searchText: String) {
        val intent = Intent(this, SearchResultsActivity::class.java)
        intent.putExtra(SearchResultsActivity.SEARCH_QUERY, SearchQuery(searchText, SearchQueryType.GENERAL))
        startActivity(intent)
    }

    override fun onConnected(bundle: Bundle?) {
        requestLocation()
    }

    private fun startNearbyStopsActivity() {
        if (isLocationEnabled && isGooglePlayServicesAvailable) {
            val intent = Intent(this, SearchResultsActivity::class.java)
            val location = latestLocation

            if (location != null) {
                intent.putExtra(SearchResultsActivity.SEARCH_QUERY, SearchQuery("Nearby Stops", SearchQueryType.NEARBY))
                startActivity(intent)
            } else {
                showShortToaster(GoogleApiActivity.ACQUIRING_LOCATION)
            }
        } else {
            showLongToaster(GoogleApiActivity.LOCATION_SERVICES_NOT_AVAILABLE)
        }
    }

    override fun onLocationChanged(location: Location) {

    }

    companion object {
        private const val stops_on_home_message_shown_flag = "show_initial_frequent_stops_on_home_notification"
        private const val prefFavouritesOnHome = "pref_show_favourites_on_home"
    }
}
