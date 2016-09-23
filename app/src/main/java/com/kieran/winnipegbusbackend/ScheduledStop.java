package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ScheduledStop implements Serializable {
    private final static String ARRIVAL_TAG = "arrival";
    private final static String DEPARTURE_TAG = "departure";
    private final static String STOP_TIMES_TAG = "times";
    private final static String ESTIMATED_TAG = "estimated";
    private final static String SCHEDULED_TAG = "scheduled";
    private final static String BUS_INFO_TAG = "bus";
    private final static String BIKE_RACK_TAG = "bike-rack";
    private final static String EASY_ACCESS_TAG = "easy-access";
    private final static String STOP_KEY_TAG = "key";
    private final static String VARIANT_NAME_TAG = "name";
    private final static String VARIANT_TAG = "variant";
    private final static String VARIANT_KEY_TAG = "key";

    private String routeVariantName;
    private StopTime estimatedArrivalTime;
    private StopTime estimatedDepartureTime;
    private StopTime scheduledArrivalTime;
    private StopTime scheduledDepartureTime;
    private boolean hasBikeRack;
    private boolean hasEasyAccess;
    private ScheduledStopKey key;
    private RouteSchedule parentRoute;
    private RouteKey routeKey;

     public ScheduledStop(JSONObject stop, RouteSchedule parentRoute) {
        this.routeVariantName = parentRoute.getRouteName();
        this.parentRoute = parentRoute;

        loadTimes(stop);
        loadVariantInfo(stop);
        loadBusInfo(stop);
        loadKey(stop);
    }

    private void loadKey(JSONObject stop) {
        try {
            key = new ScheduledStopKey(stop.getString(STOP_KEY_TAG));
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadBusInfo(JSONObject stop) {
        try {
            JSONObject bus = stop.getJSONObject(BUS_INFO_TAG);

            hasEasyAccess = bus.getBoolean(EASY_ACCESS_TAG);
            hasBikeRack = bus.getBoolean(BIKE_RACK_TAG);
        } catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadVariantInfo(JSONObject stop) {
        try {
            JSONObject variant = stop.getJSONObject(VARIANT_TAG);
            routeVariantName = variant.getString(VARIANT_NAME_TAG);
            routeKey = new RouteKey(variant.getString(VARIANT_KEY_TAG));
        }catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    private void loadTimes(JSONObject stop) {
        try {
            JSONObject times = stop.getJSONObject(STOP_TIMES_TAG);

            JSONObject departure = times.getJSONObject(DEPARTURE_TAG);

            estimatedDepartureTime = StopTime.convertStringToStopTime(departure.getString(ESTIMATED_TAG));
            scheduledDepartureTime = StopTime.convertStringToStopTime(departure.getString(SCHEDULED_TAG));


            if (times.has(ARRIVAL_TAG)) {
                JSONObject arrival = times.getJSONObject(ARRIVAL_TAG);
                scheduledArrivalTime = StopTime.convertStringToStopTime(arrival.getString(ESTIMATED_TAG));
                estimatedArrivalTime = StopTime.convertStringToStopTime(arrival.getString(SCHEDULED_TAG));
            }
        }catch (JSONException ex) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public String getTimeStatus() {
        return StopTime.getTimeStatus(estimatedDepartureTime, scheduledDepartureTime);
    }

    public RouteKey getRouteKey() {
        return routeKey;
    }

    public String getRouteVariantName() {
        return routeVariantName;
    }

    public StopTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public StopTime getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }

    public StopTime getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public StopTime getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public int getRouteNumber() {
        return parentRoute.getRouteNumber();
    }

    public boolean hasBikeRack() {
        return hasBikeRack;
    }

    public boolean hasEasyAccess() {
        return hasEasyAccess;
    }

    public boolean hasArrivalTime() {
        return estimatedArrivalTime != null && scheduledArrivalTime != null;
    }

    public ScheduledStopKey getKey() {
        return key;
    }

    public CoverageTypes getCoverageType() {
        return parentRoute.getCoverageType();
    }
}