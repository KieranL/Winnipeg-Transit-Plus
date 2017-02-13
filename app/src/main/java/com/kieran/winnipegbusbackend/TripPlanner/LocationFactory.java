package com.kieran.winnipegbusbackend.TripPlanner;

import com.kieran.winnipegbusbackend.TripPlanner.classes.Address;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Intersection;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Monument;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Stop;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationFactory {

    public static final String MONUMENT = "monument";
    public static final String ADDRESS = "address";
    public static final String INTERSECTION = "intersection";
    public static final String POINT = "point";
    public static final String STOP = "stop";

    public static Location createLocation(JSONObject location) {
        try {
            if(location.has("origin"))
                location = location.getJSONObject("origin");
            else if(location.has("destination"))
                location = location.getJSONObject("destination");

            if(location.has("type")) {
                String type = location.getString("type");
                if (type.equals(STOP))
                    return new Stop(location);
                else if (type.equals(MONUMENT))
                    return new Monument(location);
                else if (type.equals(ADDRESS))
                    return new Address(location);
                else if (type.equals(INTERSECTION))
                    return new Intersection(location);
                else if (type.equals(POINT))
                    return new Location(location);
            }

            if (location.has(STOP))
                return new Stop(location.getJSONObject(STOP));
            else if (location.has(MONUMENT))
                return new Monument(location.getJSONObject(MONUMENT));
            else if (location.has(ADDRESS))
                return new Address(location.getJSONObject(ADDRESS));
            else if (location.has(INTERSECTION))
                return new Intersection(location.getJSONObject(INTERSECTION));
            else if (location.has(POINT))
                return new Location(location.getJSONObject(POINT));
        } catch (JSONException e) {

        }

        return null;
    }
}
