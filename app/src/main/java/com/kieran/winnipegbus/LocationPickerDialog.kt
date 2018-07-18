package com.kieran.winnipegbus

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast

import com.kieran.winnipegbus.activities.GoogleApiActivity
import com.kieran.winnipegbusbackend.FavouriteStopsList
import com.kieran.winnipegbusbackend.LoadResult
import com.kieran.winnipegbusbackend.TransitApiManager
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.TripPlanner.classes.StopLocation

import org.json.JSONException
import org.json.JSONObject
import android.support.v4.app.ActivityCompat.startActivityForResult
import com.google.android.gms.location.places.ui.PlacePicker


class LocationPickerDialog(private val context: GoogleApiActivity, private val listener: OnLocationPickedListener) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_picker_dialog)
        (findViewById<View>(R.id.location_searchView) as SearchView).isIconified = false
        findViewById<View>(R.id.current_location_button).setOnClickListener(this)
        findViewById<View>(R.id.from_favourites_button).setOnClickListener(this)

        val self = this
        val originSearchView = findViewById<View>(R.id.location_searchView) as SearchView
        originSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                submitQuery(query, self)

                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    private fun submitQuery(query: String, self: LocationPickerDialog) {
        val url = TransitApiManager.generateLocationQueryUrl(query)
        TransitApiManager.getJsonAsync(url, object : TransitApiManager.OnJsonLoadResultReceiveListener {
            override fun onReceive(result: LoadResult<JSONObject>) {
                val builder = AlertDialog.Builder(context)

                try {
                    val locationNodes = result.result!!.getJSONArray("locations")
                    val locations = (0 until locationNodes.length()).map { LocationFactory.createLocation(locationNodes.getJSONObject(it)) }


                    if (locations.any()) {
                        val charSequence = arrayOfNulls<CharSequence>(locations.size)

                        for (i in charSequence.indices)
                            charSequence[i] = locations[i].title

                        builder.setItems(charSequence) { dialog, which ->
                            listener.OnLocationPicked(locations[which])
                            self.dismiss()
                            dismiss()
                        }

                        builder.create().show()
                    } else {
                        Toast.makeText(context, R.string.no_location_found, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }


    override fun onClick(v: View) {
        val self = this
        when (v.id) {
            R.id.current_location_button ->  {
                val PLACE_PICKER_REQUEST = 1
                val builder = PlacePicker.IntentBuilder()

                context.startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST)
//                val deviceLocation = context.latestLocation
//
//                if (deviceLocation != null) {
//                    val location = Location(deviceLocation, context.getString(R.string.current_location))
//                    listener.OnLocationPicked(location)
//                    dismiss()
//                } else {
//                    context.showShortToaster(GoogleApiActivity.ACQUIRING_LOCATION)
//                }
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
                        override fun onReceive(result: LoadResult<JSONObject>) {
                            try {
                                val stopNode = result.result!!.getJSONObject("stop")
                                val stop = StopLocation(stopNode)
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

    companion object {
        fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        }
    }
}
