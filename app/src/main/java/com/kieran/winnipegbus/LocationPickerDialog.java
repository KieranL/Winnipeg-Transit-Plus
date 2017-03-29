package com.kieran.winnipegbus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.kieran.winnipegbus.Activities.GoogleApiActivity;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationPickerDialog extends Dialog implements View.OnClickListener {
    private GoogleApiActivity context;
    private OnLocationPickedListener listener;

    public LocationPickerDialog(GoogleApiActivity context, OnLocationPickedListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_picker_dialog);
        ((SearchView) findViewById(R.id.location_searchView)).setIconified(false);
        findViewById(R.id.current_location_button).setOnClickListener(this);
        findViewById(R.id.from_favourites_button).setOnClickListener(this);

        final Dialog self = this;
                final SearchView originSearchView = (SearchView) findViewById(R.id.location_searchView);
        originSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url = TransitApiManager.generateLocationQueryUrl(query);
                TransitApiManager.getJsonAsync(url, new TransitApiManager.OnJsonLoadResultReceiveListener() {
                    @Override
                    public void OnReceive(LoadResult<JSONObject> result) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        try {
                            JSONArray locationNodes = result.getResult().getJSONArray("locations");
                            final List<Location> locations = new ArrayList<>();
                            for(int i = 0; i < locationNodes.length(); i++) {
                                locations.add(LocationFactory.createLocation(locationNodes.getJSONObject(i)));
                            }


                            CharSequence charSequence[] = new CharSequence[locations.size()];

                            for (int i = 0; i < charSequence.length; i++)
                                charSequence[i] = locations.get(i).getTitle();

                            builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    listener.OnLocationPickedListener(locations.get(which));
                                    self.dismiss();
                                    dismiss();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        builder.create().show();

                        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(originSearchView.getWindowToken(), 0);
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        final Dialog self = this;
        switch (v.getId()) {
            case R.id.current_location_button:
                if(context.isLocationEnabled() && context.isGooglePlayServicesAvailable()) {
                    android.location.Location deviceLocation = context.getLatestLocation();

                    if (deviceLocation != null) {
                        Location location = new Location(deviceLocation, "Current Location");
                        listener.OnLocationPickedListener(location);
                        dismiss();
                    }else {
                        context.showShortToaster(GoogleApiActivity.ACQUIRING_LOCATION);
                    }
                }else {
                    context.showLongToaster(GoogleApiActivity.LOCATION_SERVICES_NOT_AVAILABLE);
                }
                break;
            case R.id.from_favourites_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final List<FavouriteStop> favouriteStops = FavouriteStopsList.getFavouriteStopsSorted(context.getSortPreference());

                CharSequence charSequence[] = new CharSequence[favouriteStops.size()];

                for (int i = 0; i < charSequence.length; i++)
                    charSequence[i] = favouriteStops.get(i).getNumber() + " - " + favouriteStops.get(i).getDisplayName();

                builder.setItems(charSequence, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavouriteStop favouriteStop = favouriteStops.get(which);
                        TransitApiManager.getJsonAsync(TransitApiManager.generateFindStopUrl(favouriteStop.getNumber()), new TransitApiManager.OnJsonLoadResultReceiveListener() {
                            @Override
                            public void OnReceive(LoadResult<JSONObject> result) {
                                try {
                                    JSONObject stopNode = result.getResult().getJSONObject("stop");
                                    Stop stop = new Stop(stopNode);
                                    listener.OnLocationPickedListener(stop);
                                    self.dismiss();
                                    dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                builder.create().show();
                break;

        }
    }

    public interface OnLocationPickedListener {
        void OnLocationPickedListener(Location location);
    }
}
