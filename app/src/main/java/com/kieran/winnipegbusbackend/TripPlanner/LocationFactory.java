package com.kieran.winnipegbusbackend.TripPlanner;

import com.kieran.winnipegbusbackend.TripPlanner.classes.Address;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Intersection;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Monument;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationFactory {
    public static Location createLocation(JSONObject location) {
        try {
            if(location.has("origin"))
                location = location.getJSONObject("origin");
            else if(location.has("destination"))
                location = location.getJSONObject("destination");

            if (location.has("stop"))
                return new Stop(location.getJSONObject("stop"));
            else if (location.has("monument"))
                return new Monument(location.getJSONObject("monument"));
            else if (location.has("address"))
                return new Address(location.getJSONObject("address"));
            else if (location.has("intersection"))
                return new Intersection(location.getJSONObject("intersection"));
            else if (location.has("point"))
                return new Location(location.getJSONObject("point"));

        } catch (JSONException e) {

        }

        return null;
    }
}
