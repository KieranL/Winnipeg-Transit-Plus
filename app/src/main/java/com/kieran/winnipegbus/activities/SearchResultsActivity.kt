package com.kieran.winnipegbus.activities

import android.app.AlertDialog
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.adapters.StopListAdapter
import com.kieran.winnipegbus.views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.GeoLocation
import com.kieran.winnipegbusbackend.common.SearchQuery
import com.kieran.winnipegbusbackend.common.Stop
import com.kieran.winnipegbusbackend.enums.SearchQueryType
import com.kieran.winnipegbusbackend.interfaces.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchResultsActivity : GoogleApiActivity(), AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener, LocationListener {
    private var adapter: StopListAdapter? = null
    private var searchQuery: SearchQuery? = null
    private var loading = true
    private var task: Job? = null
    private var swipeRefreshLayout: StyledSwipeRefresh? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adViewResId = R.id.stopsListAdView
        setContentView(R.layout.activity_search_results)

        val listView = findViewById<View>(R.id.stops_listView) as ListView

        listView.onItemClickListener = this

        adapter = StopListAdapter(this, R.layout.listview_stops_row, stops)
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
        swipeRefreshLayout!!.isRefreshing = loading
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val stop = parent.getItemAtPosition(position) as Stop
        openStopTimes(FavouriteStop(stop.name, stop.identifier))
    }

    private fun refresh() {
        if (!loading) {
            if (isOnline) {
                loading = true
                runOnUiThread {
                    swipeRefreshLayout?.isRefreshing = loading
                }
                task = GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val stops = when (searchQuery!!.searchQueryType) {
                            SearchQueryType.GENERAL -> transitService.findStop(searchQuery!!.query)
                            SearchQueryType.NEARBY -> {
                                val location = latestLocation
                                if (location != null) {
                                    transitService.findClosestStops(GeoLocation(location.latitude, location.longitude), (nearbyStopsDistance + location.accuracy).toInt())
                                } else {
                                    ArrayList()
                                }
                            }
                            SearchQueryType.ROUTE_NUMBER -> transitService.getRouteStops(transitService.parseStringToRouteIdentifier(searchQuery!!.query))
                            else -> {
                                showLongToaster(R.string.no_results_found)
                                ArrayList()
                            }
                        }

                    onDataReceived(stops)
                    } catch (ex: Exception) {
                        Logger.getLogger().error(ex, "Error refreshing search results")
                        runOnUiThread {
                            handleException(ex)
                        }
                    }
                }
            } else {
                showLongToaster(R.string.network_error)
            }
        }
        runOnUiThread {
            swipeRefreshLayout!!.isRefreshing = loading
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (task != null)
            task!!.cancel()
        loading = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_results, menu)
        if (searchQuery!!.searchQueryType != SearchQueryType.NEARBY)
            menu.findItem(R.id.loadingIcon).isVisible = false

        if (searchQuery!!.searchQueryType != SearchQueryType.NEARBY) {
            loading = false
            refresh()
        }

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
            val stop = stops[position]
            favouritesService.add(FavouriteStop(stop.name, stop.identifier, 0, stop.latLng))
        }

        alertDialog.setNegativeButton("No", null)
        alertDialog.create().show()

        return true
    }

    override fun onRefresh() {
        refresh()
    }

    override fun onConnected(bundle: Bundle?) {
        requestLocation()
    }

    override fun onLocationChanged(location: Location) {
        if (isOnline) {
            task = GlobalScope.launch(Dispatchers.IO) {try {
            val stops = transitService.findClosestStops(GeoLocation(location.latitude, location.longitude), (nearbyStopsDistance + location.accuracy).toInt())

                    onDataReceived(stops)
                } catch (ex: Exception){
                Logger.getLogger().error(ex, "Error finding closest stops")
                    runOnUiThread{
                        handleException(ex)
                    }
                }
            }
        }
    }

    private fun onDataReceived(newStops: List<Stop>) {
        if (newStops.isEmpty()) {
            runOnUiThread {
                showLongToaster(R.string.no_results_found)
            }
        }
        stops.clear()
        stops.addAll(newStops)

        runOnUiThread {
            adapter!!.notifyDataSetChanged()
            swipeRefreshLayout!!.isRefreshing = false
        }

        loading = false
    }

    companion object {
        val SEARCH_QUERY = "search_query"
        val NEARBY_STOPS = "Nearby Stops"
        val STOPS_ON_RTE = "Stops on Rte %s"
        var stops: ArrayList<Stop> = ArrayList()
    }
}
