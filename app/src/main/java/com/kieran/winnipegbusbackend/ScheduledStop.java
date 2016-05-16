package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.Serializable;

public class ScheduledStop implements Serializable {
    private final static String ARRIVAL_TAG = "arrival";
    private final static String DEPARTURE_TAG = "departure";
    private final static String ESTIMATED_TAG = "estimated";
    private final static String SCHEDULED_TAG = "scheduled";
    private final static String BIKE_RACK_TAG = "bike-rack";
    private final static String EASY_ACCESS_TAG = "easy-access";
    private final static String STOP_KEY_TAG = "key";
    private final static String VARIANT_NAME_TAG = "name";
    private final static String VARIANT_TAG= "variant";

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

    public ScheduledStop(Node stopNode, RouteSchedule parentRoute) {
        this.routeVariantName = parentRoute.getRouteName();
        this.parentRoute = parentRoute;

        loadVariantName(stopNode);
        loadDepartureTimes(stopNode);
        loadAdditionalInfo(stopNode);
        routeKey = new RouteKey(((Element) stopNode).getElementsByTagName(VARIANT_TAG).item(0));
    }

    public void loadAdditionalInfo(Node stopNode) {
        loadArrivalTimes(stopNode);
        loadBusInfo(stopNode);
        loadKey(stopNode);
    }

    private void loadKey(Node stopNode) {
        key = new ScheduledStopKey(BusUtilities.getValue(STOP_KEY_TAG, stopNode));
    }

    public void loadArrivalTimes(Node stopNode) {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(ARRIVAL_TAG).item(0);
        try {
            estimatedArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(ESTIMATED_TAG, arrivalStopNode));
            scheduledArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(SCHEDULED_TAG, arrivalStopNode));
        } catch (Exception e) {
            //TODO what to do here?
        }
    }

    private void loadDepartureTimes(Node stopNode) {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(DEPARTURE_TAG).item(0);

        estimatedDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(ESTIMATED_TAG, departureStopNode));
        scheduledDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(SCHEDULED_TAG, departureStopNode));
    }

    private void loadVariantName(Node stopNode) {
        String name = BusUtilities.getValue(VARIANT_NAME_TAG, stopNode);
        if(name != null)
            routeVariantName = name;
    }

    public void loadBusInfo(Node stopNode) {
        hasEasyAccess = Boolean.parseBoolean(BusUtilities.getValue(EASY_ACCESS_TAG, stopNode));
        hasBikeRack = Boolean.parseBoolean(BusUtilities.getValue(BIKE_RACK_TAG, stopNode));
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