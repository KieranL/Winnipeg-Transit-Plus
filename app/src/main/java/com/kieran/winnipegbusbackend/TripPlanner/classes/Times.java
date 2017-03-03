package com.kieran.winnipegbusbackend.TripPlanner.classes;

import com.kieran.winnipegbusbackend.StopTime;

import org.json.JSONException;
import org.json.JSONObject;

public class Times {
    public int totalTime;
    public int walkingTime;
    public int waitingTime;
    public int ridingTime;
    public StopTime startTime;
    public StopTime endTime;

    public Times(JSONObject times) {
        try {
            JSONObject durations = times.getJSONObject("durations");

            startTime = StopTime.convertStringToStopTime(times.getString("start"));
            endTime = StopTime.convertStringToStopTime(times.getString("end"));

            totalTime = durations.getInt("total");
            walkingTime = durations.getInt("walking");
            waitingTime = durations.getInt("waiting");
            ridingTime = durations.getInt("riding");
        } catch (JSONException e) {

        }
    }
}
