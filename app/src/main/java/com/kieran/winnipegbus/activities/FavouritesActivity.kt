package com.kieran.winnipegbus.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.EditText
import android.widget.ListView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.SQLiteFavouritesRepository
import com.kieran.winnipegbus.adapters.StopListAdapter
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouriteStopsList
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.favourites.FavouritesService

class FavouritesActivity : BaseActivity(), AdapterView.OnItemClickListener, OnItemLongClickListener {
    private var adapter: StopListAdapter? = null

    override fun onRestart() {
        super.onRestart()
        FavouriteStopsList.sort(sortPreference)
        reloadList()
    }

    override fun onResume() {
        super.onResume()
        FavouriteStopsList.sort(sortPreference)
        reloadList()
    }

    private fun reloadList() {
        FavouriteStopsList.isLoadNeeded = true
        getFavouritesList()
        adapter!!.notifyDataSetChanged()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adViewResId = R.id.stopsListAdView

        setContentView(R.layout.activity_favourite_stops)

        val listView = findViewById<ListView>(R.id.stops_listView)

        initializeAdsIfEnabled()
        getFavouritesList()

        listView.onItemClickListener = this
        listView.onItemLongClickListener = this

        adapter = StopListAdapter(this, R.layout.listview_stops_row)
        listView.adapter = adapter
    }

    private fun getFavouritesList() {
        if(true) {
            val service = FavouritesService.getInstance(SQLiteFavouritesRepository.getInstance(this))
            val stops = service.getAll()
        }else {
            FavouriteStopsList.loadFavourites()
            StopListAdapter.sortPreference = sortPreference
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favourites, menu)
        return true
    }

    private fun openStopTimesAndUse(favouriteStop: FavouriteStop?) {
        favouriteStop!!.use()
        FavouriteStopsList.saveFavouriteStops()

        openStopTimes(favouriteStop)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openStopTimesAndUse(adapter!!.getItem(position))
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val context = this
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setMessage(R.string.edit_favourite_dialog_title)
        alertDialog.setPositiveButton(R.string.delete) { _, _ ->
            FavouriteStopsList.remove((adapter!!.getItem(position)!!.identifier as WinnipegTransitStopIdentifier).stopNumber)
            reloadList()
        }

        alertDialog.setNeutralButton(R.string.rename) { _, _ ->
            val renameDialog = AlertDialog.Builder(context)
            val editText = EditText(context)
            val favouriteStop = FavouriteStopsList[position]
            editText.setText(favouriteStop.displayName)
            renameDialog.setView(editText)

            renameDialog.setNeutralButton(R.string.default_label, null)

            renameDialog.setPositiveButton(R.string.ok) { _, _ ->
                FavouriteStopsList[position].alias = editText.text.toString()
                FavouriteStopsList.saveFavouriteStops()
                reloadList()
            }
            renameDialog.setNegativeButton(R.string.cancel, null)

            val button = renameDialog.show().getButton(DialogInterface.BUTTON_NEUTRAL)
            button.setOnClickListener { editText.setText(favouriteStop.name) }
        }

        alertDialog.setNegativeButton(R.string.cancel, null)
        alertDialog.create().show()

        return true
    }
}
