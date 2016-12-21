package com.kieran.winnipegbusbackend.TripPlanner;

import com.kieran.winnipegbusbackend.TripPlanner.classes.Address;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Coordinate;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Intersection;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Monument;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationFactory {
    public static Location createLocation(JSONObject location) {
        try {
            switch (location.getString("type")) {
                default: return null;
                case "stop": return new Stop(location);
                case "monument": return new Monument(location);
                case "address": return new Address(location);
                case "intersection": return new Intersection(location);
                case "point": return new Coordinate(location);

            }
        } catch (JSONException e) {
            return null;
        }
    }
}
