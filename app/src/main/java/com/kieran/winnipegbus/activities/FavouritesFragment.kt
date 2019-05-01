package com.kieran.winnipegbus.activities

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.app.Fragment
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView

import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.adapters.StopListAdapter
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouriteStopsList
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private lateinit var adapter: StopListAdapter
    private val topFavourites = ArrayList<FavouriteStop>();

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        val listView = view.findViewById<ListView>(R.id.stops_listView)


        listView.onItemClickListener = this
        listView.onItemLongClickListener = this

        adapter = StopListAdapter(activity, R.layout.listview_stops_row, topFavourites)
        listView.adapter = adapter
        return view
    }

    override fun onResume() {
        super.onResume()
        reloadList()
    }

    private fun reloadList() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                FavouriteStopsList.isLoadNeeded = true
                val stops = FavouriteStopsList.getFavouriteStopsSorted(FavouritesListSortType.FREQUENCY_DESC, 3)

                activity.runOnUiThread {
                    topFavourites.clear()
                    topFavourites.addAll(stops)

                    adapter.notifyDataSetChanged()
                }
            }catch (ex: Exception){

            }
        }
    }

    private fun openStopTimesAndUse(favouriteStop: FavouriteStop?) {
        favouriteStop!!.use()
        FavouriteStopsList.saveFavouriteStops()

        (activity as BaseActivity).openStopTimes(favouriteStop)
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openStopTimesAndUse(adapter.getItem(position))
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val context = activity
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setMessage(R.string.edit_favourite_dialog_title)
        alertDialog.setPositiveButton(R.string.delete) { _, _ ->
            FavouriteStopsList.remove((adapter.getItem(position)!!.identifier as WinnipegTransitStopIdentifier).stopNumber)
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
