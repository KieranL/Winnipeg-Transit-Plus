package com.kieran.winnipegbusbackend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteSchedule extends Route implements Serializable {
    private final static String SCHEDULED_STOPS_TAG = "scheduled-stops";

    private List<ScheduledStop> stops;

    public RouteSchedule(RouteSchedule routeSchedule) {
        super(routeSchedule);
    }

    public RouteSchedule(JSONObject jsonObject) {
        super(jsonObject);

        stops = new ArrayList<>();

        loadScheduledStops(jsonObject);
    }

    private void loadScheduledStops(JSONObject jsonObject) {
        try {
            JSONArray scheduledStops = jsonObject.getJSONArray(SCHEDULED_STOPS_TAG);

            for (int s = 0; s < scheduledStops.length(); s++) {
                JSONObject stop = scheduledStops.getJSONObject(s);
                try {
                    stops.add(new ScheduledStop(stop, this));
                }catch (Exception e) {
                    //blank
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<ScheduledStop> getScheduledStops() {
        return stops;
    }
}
