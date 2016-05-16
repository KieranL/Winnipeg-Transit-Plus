package com.kieran.winnipegbusbackend;


import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {
    private List<FavouriteStop> stops;

    public SearchResults() {
        stops = new ArrayList<>();
    }

    public SearchResults loadStops(LoadResult result) {
        if (result.getResult() != null) {
            stops.clear();
            NodeList stops = ((Document) result.getResult()).getElementsByTagName(Stop.STOP_TAG);

            if (stops.getLength() > 0)
                for (int s = 0; s < stops.getLength(); s++) {
                    Node stop = stops.item(s);
                    FavouriteStop favouriteStop = new FavouriteStop(BusUtilities.getValue(Stop.STOP_NAME_TAG, stop), Integer.parseInt(BusUtilities.getValue(Stop.STOP_NUMBER_TAG, stop)));
                    this.stops.add(favouriteStop);

                    favouriteStop.setLatLng(getLatLng(stop));
                }
        }

        return this;
    }

    public List<FavouriteStop> getStops() {
        return stops;
    }


    private LatLng getLatLng(Node stop) {
        return new LatLng(Double.parseDouble(BusUtilities.getValue(StopSchedule.LATITUDE_TAG, stop)), Double.parseDouble(BusUtilities.getValue(StopSchedule.LONGITUDE_TAG, stop)));
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
