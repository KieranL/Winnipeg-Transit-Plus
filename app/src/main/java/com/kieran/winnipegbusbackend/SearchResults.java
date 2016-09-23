package com.kieran.winnipegbusbackend;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {
    private List<FavouriteStop> stops;

    public SearchResults() {
        stops = new ArrayList<>();
    }

    public SearchResults loadStops(LoadResult<JSONObject> result) {
        if (result.getResult() != null) {
            stops.clear();

            try {
                JSONArray stops = result.getResult().getJSONArray(Stop.STOP_TAG);

                if (stops.length() > 0)
                    for (int s = 0; s < stops.length(); s++) {
                        JSONObject stop = stops.getJSONObject(s);
                        FavouriteStop favouriteStop = new FavouriteStop(stop.getString(Stop.STOP_NAME_TAG), stop.getInt(Stop.STOP_NUMBER_TAG));
                        this.stops.add(favouriteStop);

                        favouriteStop.setLatLng(getLatLng(stop));
                    }
            } catch (JSONException e) {

            }
        }

        return this;
    }

    public List<FavouriteStop> getStops() {
        return stops;
    }

    private LatLng getLatLng(JSONObject stop) {
        try {
            JSONObject geographic = stop.getJSONObject(Stop.STOP_CENTRE_TAG).getJSONObject(Stop.GEOGRAPHIC_TAG);
            return new LatLng(geographic.getDouble(StopSchedule.LATITUDE_TAG), geographic.getDouble(StopSchedule.LONGITUDE_TAG));
        } catch (JSONException e) {
            return null;
        }
    }

    public void clear() {
        stops.clear();
    }

    public FavouriteStop get(int position) {
        return stops.get(position);
    }

    public int getLength() {
        return stops.size();
    }
}
