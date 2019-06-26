package com.kieran.winnipegbus.activities

import android.app.AlertDialog
import android.app.Fragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ListView
import com.kieran.winnipegbus.R
import com.kieran.winnipegbus.adapters.StopListAdapter
import com.kieran.winnipegbus.views.StyledSwipeRefresh
import com.kieran.winnipegbusbackend.TransitServiceProvider
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.favourites.FavouritesService
import com.kieran.winnipegbusbackend.interfaces.TransitService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavouritesFragment: Fragment(), AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private lateinit var adapter: StopListAdapter
    private val topFavourites = ArrayList<FavouriteStop>()

    private val transitService: TransitService by lazy {
        TransitServiceProvider.getTransitService()
    }

    private val favouritesService: FavouritesService by lazy {
        getBaseActivity().getFavouritesService(transitService.getAgencyId())
    }

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
        val swipeRefresh = activity.findViewById<StyledSwipeRefresh>(R.id.favourites_swipeRefresh)
        swipeRefresh.isRefreshing = true
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val stops = favouritesService.getAll(FavouritesListSortType.FREQUENCY_DESC).take(3)

                activity.runOnUiThread {
                    topFavourites.clear()
                    topFavourites.addAll(stops)

                    adapter.notifyDataSetChanged()
                    swipeRefresh.isRefreshing = false
                }
            } catch (ex: Exception) {
                activity.runOnUiThread {
                    swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun openStopTimesAndUse(favouriteStop: FavouriteStop) {
        favouriteStop.use()
        favouritesService.update(favouriteStop)

        getBaseActivity().openStopTimes(favouriteStop)
    }

    fun getBaseActivity(): BaseActivity {
        return activity as BaseActivity
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        openStopTimesAndUse(adapter.getItem(position) as FavouriteStop)
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val context = activity
        val alertDialog = AlertDialog.Builder(context)

        alertDialog.setMessage(R.string.edit_favourite_dialog_title)
        alertDialog.setPositiveButton(R.string.delete) { _, _ ->
            favouritesService.delete(adapter.getItem(position)!!.identifier)
            reloadList()
        }

        alertDialog.setNeutralButton(R.string.rename) { _, _ ->
            val renameDialog = AlertDialog.Builder(context)
            val editText = EditText(context)
            val favouriteStop = topFavourites[position]
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
