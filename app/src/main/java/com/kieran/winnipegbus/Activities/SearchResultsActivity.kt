package com.kieran.winnipegbus.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.Adapters.StopListAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.Views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.FavouriteStopsList
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.SearchQuery
import com.kieran.winnipegbusbackend.SearchResults
import com.kieran.winnipegbusbackend.Stop
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.enums.SearchQueryType

import org.json.JSONObject


class SearchResultsActivity : GoogleApiActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, LocationListener, TransitApiManager.OnJsonLoadResultReceiveListener {
    private var adapter: StopListAdapter? = null
    private var searchQuery: SearchQuery? = null
    private var loading = false
    private var task: AsyncTask<*, *, *>? = null
    private var swipeRefreshLayout: StyledSwipeRefresh? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchResults.clear()
        adViewResId = R.id.stopsListAdView
        setContentView(R.layout.activity_search_results)

        val listView = findViewById<View>(R.id.stops_listView) as ListView

        listView.onItemClickListener = this

        adapter = StopListAdapter(this, R.layout.listview_stops_row, searchResults.stops)
        listView.adapter = adapter

        listView.onItemLongClickListener = this

        searchQuery = intent.getSerializableExtra(SEARCH_QUERY) as SearchQuery

        setupSwipeRefresh()
        setupGoogleApi()
        updateTitle()
        initializeAdsIfEnabled()
    }

    private fun setupGoogleApi() {
        if (searchQuery!!.searchQueryType == SearchQueryType.NEARBY) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build()
            connectClient()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout = findViewById<View>(R.id.search_results_swipeRefresh) as StyledSwipeRefresh
        swipeRefreshLayout!!.setOnRefreshListener(this)

        if (searchQuery!!.searchQueryType != SearchQueryType.NEARBY) {
            swipeRefreshLayout!!.isEnabled = false
        }
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openStopTimes(parent.getItemAtPosition(position) as Stop)
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true

                task = TransitApiManager.getJsonAsync(searchQuery!!.queryUrl, this)
            } else {
                showLongToaster(R.string.network_error)
            }
        }
        swipeRefreshLayout!!.isRefreshing = loading
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (task != null)
            task!!.cancel(true)
        loading = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_results, menu)
        if (searchQuery!!.searchQueryType != SearchQueryType.NEARBY)
            menu.findItem(R.id.loadingIcon).isVisible = false

        refresh()

        return true
    }

    private fun updateTitle() {
        if (searchQuery!!.searchQueryType == SearchQueryType.ROUTE_NUMBER)
            title = String.format(STOPS_ON_RTE, searchQuery!!.query)
        else if (searchQuery!!.searchQueryType == SearchQueryType.NEARBY)
            title = NEARBY_STOPS
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favourites -> {
                openFavourites()
                return true
            }
            R.id.map -> {
                if (!loading)
                    openMap()
                else
                    showLongToaster(R.string.wait_for_load)
                return true
            }
            R.id.loadingIcon -> {
                refresh()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun openMap() {
        val intent = Intent(this, StopsMapActivity::class.java)

        startActivity(intent)
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setMessage("Add to Favourites?")
        alertDialog.setPositiveButton("Yes") { dialogInterface, which ->
            FavouriteStopsList.loadFavourites()
            FavouriteStopsList.addToFavourites(searchResults.get(position))
        }

        alertDialog.setNegativeButton("No", null)
        alertDialog.create().show()

        return true
    }

    override fun onRefresh() {
        refresh()
    }

    override fun onConnected(bundle: Bundle) {
        requestLocation()
    }

    override fun onLocationChanged(location: Location) {
        searchQuery = TransitApiManager.generateSearchQuery(location, nearbyStopsDistance)
    }

    override fun OnReceive(result: LoadResult<JSONObject>) {
        if (loading) {
            if (result.result != null) {
                searchResults.loadStops(result)

                if (searchResults.length <= 0) {
                    showLongToaster(R.string.no_results_found)
                }
            } else if (result.exception != null) {
                handleException(result.exception)
            }
        }

        adapter!!.notifyDataSetChanged()
        swipeRefreshLayout!!.isRefreshing = false
        loading = false
    }

    companion object {
        val SEARCH_QUERY = "search_query"
        val NEARBY_STOPS = "Nearby Stops"
        val STOPS_ON_RTE = "Stops on Rte %s"
        var searchResults = SearchResults()
    }
}
