package com.kieran.winnipegbusbackend;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StopSchedule extends Stop {
    private final static String STOP_TAG = "stop";
    private final static String STOP_SCHEDULE_TAG = "stop-schedule";
    private final static String ROUTES_TAG = "route-schedules";
    public final static String LATITUDE_TAG = "latitude";
    public final static String LONGITUDE_TAG = "longitude";

    private List<RouteSchedule> routeList = new ArrayList<>();
    private LatLng latLng;

    public StopSchedule(JSONObject jsonObject) {
        try {
            jsonObject = jsonObject.getJSONObject(STOP_SCHEDULE_TAG);
            loadStopNumber(jsonObject.getJSONObject(STOP_TAG));
            loadStopName(jsonObject.getJSONObject(STOP_TAG));
            loadRoutes(jsonObject);
            loadLatLng(jsonObject.getJSONObject(STOP_TAG).getJSONObject(STOP_CENTRE_TAG).getJSONObject(GEOGRAPHIC_TAG));
        } catch (JSONException e) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public StopSchedule(JSONObject jsonObject, int stopNumber) {
        super(stopNumber);
        try {
            jsonObject = jsonObject.getJSONObject(STOP_SCHEDULE_TAG);
            loadStopName(jsonObject.getJSONObject(STOP_TAG));
            loadRoutes(jsonObject);
            loadLatLng(jsonObject.getJSONObject(STOP_TAG).getJSONObject(STOP_CENTRE_TAG).getJSONObject(GEOGRAPHIC_TAG));
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadLatLng(JSONObject jsonObject) throws JSONException {
        try {
            latLng = new LatLng(jsonObject.getDouble(LATITUDE_TAG), jsonObject.getDouble(LONGITUDE_TAG));
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadRoutes(JSONObject jsonObject) throws JSONException {
        try {
            JSONArray routes = jsonObject.getJSONArray(ROUTES_TAG);

            for (int r = 0; r < routes.length(); r++)
                routeList.add(new RouteSchedule(routes.getJSONObject(r)));
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadStopName(JSONObject jsonObject) throws JSONException {
        try {
            stopName = jsonObject.getString(STOP_NAME_TAG);
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadStopNumber(JSONObject jsonObject) throws JSONException {
        try {
            stopNumber = jsonObject.getInt(STOP_NUMBER_TAG);
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public List<RouteSchedule> getRouteList() {
        return routeList;
    }

    public List<ScheduledStop> getScheduledStops() {
        List<ScheduledStop> scheduledStops = new ArrayList<>();

        for (RouteSchedule r : routeList)
            scheduledStops.addAll(r.getScheduledStops());

        return scheduledStops;
    }

    public List<ScheduledStop> getScheduledStopsSorted() {
        List<ScheduledStop> scheduledStops = getScheduledStops();

        Collections.sort(scheduledStops, new Comparator<ScheduledStop>() {
            @Override
            public int compare(ScheduledStop stop1, ScheduledStop stop2) {
                return stop1.getEstimatedDepartureTime().compareTo(stop2.getEstimatedDepartureTime());
            }
        });

        return scheduledStops;
    }

    public ScheduledStop getScheduledStopByKey(ScheduledStopKey key) {
        for (ScheduledStop scheduledStop : getScheduledStops())
            if (scheduledStop.getKey().equals(key))
                return scheduledStop;
        return null;
    }

    public StopFeatures createStopFeatures() {
        return new StopFeatures(stopNumber, stopName, getLatLng());
    }

    private LatLng getLatLng() {
        return latLng;
    }

    public void refresh(JSONObject jsonObject) {
        routeList.clear();

        try {
            jsonObject = jsonObject.getJSONObject(STOP_SCHEDULE_TAG);
            loadRoutes(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
