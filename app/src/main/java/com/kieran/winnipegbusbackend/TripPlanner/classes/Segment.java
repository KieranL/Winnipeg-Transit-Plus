package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Segment {
    StopTime start;
    StopTime end;
    Location from;
    Location to;

    public Segment(JSONObject object) {
        try {
            JSONObject times = object.getJSONObject("times");
            start = StopTime.convertStringToStopTime(times.getString("start"));
            end = StopTime.convertStringToStopTime(times.getString("end"));

            from = LocationFactory.createLocation(object.getJSONObject("from"));
            to = LocationFactory.createLocation(object.getJSONObject("to"));

        }catch (JSONException ex) {

        }
    }
}
