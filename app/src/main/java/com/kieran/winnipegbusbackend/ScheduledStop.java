package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScheduledStop {

    private final static String ARRIVAL_TAG = "arrival";
    private final static String DEPARTURE_TAG = "departure";
    private final static String ESTIMATED_TAG = "estimated";
    private final static String SCHEDULED_TAG = "scheduled";

    private Node stopNode;
    private String routeVariantName;
    private StopTime estimatedArrivalTime;
    private StopTime estimatedDepartureTime;
    private StopTime scheduledArrivalTime;
    private StopTime scheduledDepartureTime;
    private boolean hasBikeRack;
    private boolean hasEasyAccess;
    private boolean hasArrivalTime;
    private ScheduledStopKey key;
    private RouteSchedule parentRoute;

    public ScheduledStop(Node stopNode, RouteSchedule parentRoute) {
        this.stopNode = stopNode;
        this.routeVariantName = parentRoute.getRouteName();
        this.parentRoute = parentRoute;

        loadVariantName();
        loadDepartureTimes();
    }

    public void loadAdditionalInfo() {
        loadArrivalTimes();
        loadBusInfo();
        loadKey();
    }

    private void loadKey() {
        key = new ScheduledStopKey(BusUtilities.getValue(StopTimesNodeTags.KEY.tag, stopNode));
    }

    public void loadArrivalTimes() {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(ARRIVAL_TAG).item(0);
        try {
            estimatedArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(ESTIMATED_TAG, arrivalStopNode));
            scheduledArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(SCHEDULED_TAG, arrivalStopNode));
            hasArrivalTime = true;
        } catch (Exception e) {
            hasArrivalTime = false;
        }
    }

    private void loadDepartureTimes() {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(DEPARTURE_TAG).item(0);

        estimatedDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(ESTIMATED_TAG, departureStopNode));
        scheduledDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(SCHEDULED_TAG, departureStopNode));
    }

    private void loadVariantName() {
        String name = BusUtilities.getValue(StopTimesNodeTags.VARIANT_NAME.tag, stopNode);
        if(name != null)
            routeVariantName = name;
    }

    public void loadBusInfo() {
        hasEasyAccess = Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.EASY_ACCESS.tag, stopNode));
        hasBikeRack = Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.BIKE_RACK.tag, stopNode));
    }

    public int getTimeBehindInMinutes() {
        return StopTime.timeBehindMinutes(estimatedDepartureTime, scheduledDepartureTime);
    }

    public String getTimeStatus() {
        return StopTime.getTimeStatus(estimatedDepartureTime, scheduledDepartureTime);
    }

    public RouteKey getRouteKey() {
        return new RouteKey(((Element) stopNode).getElementsByTagName(StopTimesNodeTags.VARIANT.tag).item(0));
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
        return hasArrivalTime;
    }

    public ScheduledStopKey getKey() {
        if(key == null)
            loadKey();
        return key;
    }

    public int getCoverageTypeId() {
        return parentRoute.getCoverageType();
    }
}