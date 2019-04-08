package com.kieran.winnipegbus.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.common.Stop
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import android.webkit.WebViewClient
import com.kieran.winnipegbusbackend.common.SearchQuery
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.enums.SearchQueryType
import com.kieran.winnipegbusbackend.enums.SupportedFeature
import com.kieran.winnipegbusbackend.interfaces.TransitService


class HomeScreenActivity : GoogleApiActivity(), LocationListener {
    private var searchButton: Button? = null
    private var searchField: EditText? = null
    private lateinit var transitService: TransitService

    private val isSearchEnabled: Boolean
        get() = searchField!!.text.isNotEmpty()

    override fun onRestart() {
        super.onRestart()

        updateGoButtonStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transitService = TransitServiceProvider.getTransitService()
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
                alert.setTitle("Privacy Policy")

                val wv = WebView(this)
                wv.loadUrl(getString(R.string.privacy_url))
                wv.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        view.loadUrl(url)

                        return true
                    }
                }

                alert.setView(wv)
                alert.setNegativeButton("Close", { dialog, id -> dialog.dismiss() })
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
                openStopTimes(Stop(transitService.parseStringToStopIdentifier(searchText)))
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
}
