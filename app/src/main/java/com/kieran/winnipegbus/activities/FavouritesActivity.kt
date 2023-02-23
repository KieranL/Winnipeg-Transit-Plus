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
import com.kieran.winnipegbus.adapters.StopListAdapter
import com.kieran.winnipegbusbackend.common.FavouriteStop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouritesActivity : BaseActivity(), AdapterView.OnItemClickListener, OnItemLongClickListener {
    private var adapter: StopListAdapter? = null
    private val favouriteStops = ArrayList<FavouriteStop>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adViewResId = R.id.stopsListAdView

        setContentView(R.layout.activity_favourite_stops)

        val listView = findViewById<ListView>(R.id.stops_listView)

        initializeAdsIfEnabled()

        listView.onItemClickListener = this
        listView.onItemLongClickListener = this

        adapter = StopListAdapter(this, R.layout.listview_stops_row, favouriteStops)
        listView.adapter = adapter
    }

    override fun onRestart() {
        super.onRestart()
        reloadList()
    }

    override fun onResume() {
        super.onResume()
        reloadList()
    }

    private fun reloadList() {
        GlobalScope.launch(Dispatchers.IO) {
            val stops = favouritesService.getAll(sortPreference)

            runOnUiThread {
                favouriteStops.clear()
                favouriteStops.addAll(stops)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_favourites, menu)
        return true
    }

    private fun openStopTimesAndUse(favouriteStop: FavouriteStop) {
        favouriteStop.use()
        favouritesService.update(favouriteStop)

        openStopTimes(favouriteStop)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openStopTimesAndUse(adapter!!.getItem(position) as FavouriteStop)
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val context = this
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setMessage(R.string.edit_favourite_dialog_title)
        alertDialog.setPositiveButton(R.string.delete) { _, _ ->
            favouritesService.delete((adapter!!.getItem(position)!!.id))
            reloadList()
        }

        alertDialog.setNeutralButton(R.string.rename) { _, _ ->
            val renameDialog = AlertDialog.Builder(context)
            val editText = EditText(context)
            val favouriteStop = favouriteStops[position]
            editText.setText(favouriteStop.displayName)
            renameDialog.setView(editText)

            renameDialog.setNeutralButton(R.string.default_label, null)

            renameDialog.setPositiveButton(R.string.ok) { _, _ ->
                favouriteStop.alias = editText.text.toString()
                favouritesService.update(favouriteStop)
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
