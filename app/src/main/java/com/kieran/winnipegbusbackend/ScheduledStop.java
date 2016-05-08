package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.Serializable;

public class ScheduledStop implements Serializable {
    private final static String ARRIVAL_TAG = "arrival";
    private final static String DEPARTURE_TAG = "departure";
    private final static String ESTIMATED_TAG = "estimated";
    private final static String SCHEDULED_TAG = "scheduled";

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
        routeKey = new RouteKey(((Element) stopNode).getElementsByTagName(StopTimesNodeTags.VARIANT.tag).item(0));
    }

    public void loadAdditionalInfo(Node stopNode) {
        loadArrivalTimes(stopNode);
        loadBusInfo(stopNode);
        loadKey(stopNode);
    }

    private void loadKey(Node stopNode) {
        key = new ScheduledStopKey(BusUtilities.getValue(StopTimesNodeTags.KEY.tag, stopNode));
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
        String name = BusUtilities.getValue(StopTimesNodeTags.VARIANT_NAME.tag, stopNode);
        if(name != null)
            routeVariantName = name;
    }

    public void loadBusInfo(Node stopNode) {
        hasEasyAccess = Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.EASY_ACCESS.tag, stopNode));
        hasBikeRack = Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.BIKE_RACK.tag, stopNode));
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