package com.kieran.winnipegbus

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.SearchView

import com.kieran.winnipegbus.Activities.GoogleApiActivity
import com.kieran.winnipegbusbackend.FavouriteStop
import com.kieran.winnipegbusbackend.FavouriteStopsList
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class LocationPickerDialog(private val context: GoogleApiActivity, private val listener: OnLocationPickedListener) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_picker_dialog)
        (findViewById<View>(R.id.location_searchView) as SearchView).isIconified = false
        findViewById<View>(R.id.current_location_button).setOnClickListener(this)
        findViewById<View>(R.id.from_favourites_button).setOnClickListener(this)

        val self = this
        val originSearchView = findViewById<View>(R.id.location_searchView) as SearchView
        originSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val url = TransitApiManager.generateLocationQueryUrl(query)
                TransitApiManager.getJsonAsync(url, object : TransitApiManager.OnJsonLoadResultReceiveListener {
                    override fun OnReceive(result: LoadResult<JSONObject>) {
                        val builder = AlertDialog.Builder(context)

                        try {
                            val locationNodes = result.result!!.getJSONArray("locations")
                            val locations = ArrayList<Location>()
                            for (i in 0 until locationNodes.length()) {
                                locations.add(LocationFactory.createLocation(locationNodes.getJSONObject(i))!!)
                            }


                            val charSequence = arrayOfNulls<CharSequence>(locations.size)

                            for (i in charSequence.indices)
                                charSequence[i] = locations[i].title

                            builder.setItems(charSequence) { dialog, which ->
                                listener.OnLocationPicked(locations[which])
                                self.dismiss()
                                dismiss()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }

                        builder.create().show()
                    }
                })

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }


    override fun onClick(v: View) {
        val self = this
        when (v.id) {
            R.id.current_location_button -> if (context.isLocationEnabled && context.isGooglePlayServicesAvailable) {
                val deviceLocation = context.latestLocation

                if (deviceLocation != null) {
                    val location = Location(deviceLocation, context.getString(R.string.current_location))
                    listener.OnLocationPicked(location)
                    dismiss()
                } else {
                    context.showShortToaster(GoogleApiActivity.ACQUIRING_LOCATION)
                }
            } else {
                context.showLongToaster(GoogleApiActivity.LOCATION_SERVICES_NOT_AVAILABLE)
            }
            R.id.from_favourites_button -> {
                val builder = AlertDialog.Builder(context)
                val favouriteStops = FavouriteStopsList.getFavouriteStopsSorted(context.sortPreference)

                val charSequence = arrayOfNulls<CharSequence>(favouriteStops.size)

                for (i in charSequence.indices)
                    charSequence[i] = favouriteStops[i].number.toString() + " - " + favouriteStops[i].displayName

                builder.setItems(charSequence) { dialog, which ->
                    val favouriteStop = favouriteStops[which]
                    self.dismiss()
                    dismiss()
                    TransitApiManager.getJsonAsync(TransitApiManager.generateFindStopUrl(favouriteStop.number), object : TransitApiManager.OnJsonLoadResultReceiveListener {
                        override fun OnReceive(result: LoadResult<JSONObject>) {
                            try {
                                val stopNode = result.result!!.getJSONObject("stop")
                                val stop = Stop(stopNode)
                                listener.OnLocationPicked(stop)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    })
                }

                builder.create().show()
            }
        }
    }

    interface OnLocationPickedListener {
        fun OnLocationPicked(location: Location)
    }
}
