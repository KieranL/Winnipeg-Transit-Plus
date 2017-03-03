package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.Route;

import org.json.JSONObject;

public class RideSegment extends Segment {
    Route route;

    public RideSegment(JSONObject segment) {
        super(segment);

        route = new Route(segment);
    }

    @Override
    public String toString() {
        return route.getRouteName();
    }
}
