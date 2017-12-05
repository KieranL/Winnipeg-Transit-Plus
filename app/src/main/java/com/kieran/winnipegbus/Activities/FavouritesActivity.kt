package com.kieran.winnipegbus.Activities

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

import com.kieran.winnipegbus.Adapters.StopListAdapter
import com.kieran.winnipegbus.R
import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.FavouriteStopsList

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

        val listView = findViewById<View>(R.id.stops_listView) as ListView

        initializeAdsIfEnabled()
        getFavouritesList()

        listView.onItemClickListener = this
        listView.onItemLongClickListener = this

        adapter = StopListAdapter(this, R.layout.listview_stops_row)
        listView.adapter = adapter
    }

    private fun getFavouritesList() {
        FavouriteStopsList.loadFavourites()
        StopListAdapter.sortPreference = sortPreference
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

        alertDialog.setMessage("Edit this Favourite?")
        alertDialog.setPositiveButton("Delete") { dialogInterface, which ->
            FavouriteStopsList.remove(adapter!!.getItem(position)!!.number)
            reloadList()
        }

        alertDialog.setNeutralButton("Rename") { dialogInterface, which ->
            val renameDialog = AlertDialog.Builder(context)
            val editText = EditText(context)
            val favouriteStop = FavouriteStopsList.get(position)
            editText.setText(favouriteStop.displayName)
            renameDialog.setView(editText)

            renameDialog.setNeutralButton("Default", null)

            renameDialog.setPositiveButton("Ok") { dialog, which ->
                FavouriteStopsList.get(position).alias = editText.text.toString()
                FavouriteStopsList.saveFavouriteStops()
                reloadList()
            }
            renameDialog.setNegativeButton("Cancel", null)

            val button = renameDialog.show().getButton(DialogInterface.BUTTON_NEUTRAL)
            button.setOnClickListener { editText.setText(favouriteStop.name) }
        }

        alertDialog.setNegativeButton("Cancel", null)
        alertDialog.create().show()

        return true
    }
}
