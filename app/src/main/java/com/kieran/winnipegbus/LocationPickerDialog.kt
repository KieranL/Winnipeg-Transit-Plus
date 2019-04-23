package com.kieran.winnipegbus

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.kieran.winnipegbus.activities.GoogleApiActivity
import com.kieran.winnipegbus.activities.MapActivity
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouriteStopsList
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TransitApiManager
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes.Location
import com.kieran.winnipegbusbackend.agency.winnipegtransit.TripPlanner.classes.StopLocation
import com.kieran.winnipegbusbackend.agency.winnipegtransit.WinnipegTransitStopIdentifier
import com.kieran.winnipegbusbackend.common.LoadResult
import org.json.JSONException
import org.json.JSONObject


class LocationPickerDialog(private val context: GoogleApiActivity, private val listener: OnLocationPickedListener) : Dialog(context), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_picker_dialog)
        findViewById<View>(R.id.current_location_button).setOnClickListener(this)
        findViewById<View>(R.id.from_favourites_button).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.current_location_button -> {
                val builder = PlacePicker.IntentBuilder()
                val bounds = LatLngBounds.Builder()
                bounds.include(LatLng(MapActivity.DEFAULT_LATITUDE, MapActivity.DEFAULT_LONGITUDE))
                builder.setLatLngBounds(bounds.build())
                context.startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST)
            }
            R.id.from_favourites_button -> {
                val builder = AlertDialog.Builder(context)
                val favouriteStops = FavouriteStopsList.getFavouriteStopsSorted(context.sortPreference)

                val charSequence = arrayOfNulls<CharSequence>(favouriteStops.size)

                for (i in charSequence.indices)
                    charSequence[i] = favouriteStops[i].identifier.toString() + " - " + favouriteStops[i].displayName

                builder.setItems(charSequence) { dialog, which ->
                    val favouriteStop = favouriteStops[which]
                    dismiss()
                    TransitApiManager.getJsonAsync(TransitApiManager.generateFindStopUrl((favouriteStop.identifier as WinnipegTransitStopIdentifier).stopNumber), object : TransitApiManager.OnJsonLoadResultReceiveListener {
                        override fun onReceive(result: LoadResult<JSONObject>) {
                            try {
                                val stopNode = result.result!!.getJSONObject("stop")
                                val stop = StopLocation(stopNode)
                                listener.onLocationPicked(stop)
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

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(data, context)
                val location = Location(place.latLng, place.name.toString())

                dismiss()
                listener.onLocationPicked(location)
            } else if (resultCode != RESULT_CANCELED) {
                Toast.makeText(context, R.string.unable_to_find_place, Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface OnLocationPickedListener {
        fun onLocationPicked(location: Location)
    }

    companion object {
        private val PLACE_PICKER_REQUEST = 1
    }
}
