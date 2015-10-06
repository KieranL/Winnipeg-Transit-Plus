package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScheduledStop {

    private Node stopNode;
    private String routeVariantName;
    private StopTime estimatedArrivalTime;
    private StopTime estimatedDepartureTime;
    private StopTime scheduledArrivalTime;
    private StopTime scheduledDepartureTime;
    private int routeNumber;
    private boolean hasBikeRack;
    private boolean hasEasyAccess;
    private boolean hasArrivalTime;

    public ScheduledStop(Node stopNode, int routeNumber, String routeName) {
        this.routeNumber = routeNumber;
        this.stopNode = stopNode;
        this.routeVariantName = routeName;

        loadVariantName();
        loadDepartureTimes();
    }

    public void loadAdditionalInfo() {
        loadArrivalTimes();
        loadBusInfo();
    }

    public void loadArrivalTimes() {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.ARRIVAL.tag).item(0);
        try {
            estimatedArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.ESTIMATED.tag, arrivalStopNode));
            scheduledArrivalTime = BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.SCHEDULED.tag, arrivalStopNode));
            hasArrivalTime = true;
        } catch (Exception e) {
            hasArrivalTime = false;
        }
    }

    private void loadDepartureTimes() {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.DEPARTURE.tag).item(0);

        estimatedDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.ESTIMATED.tag, departureStopNode));
        scheduledDepartureTime = BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.SCHEDULED.tag, departureStopNode));
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
        return routeNumber;
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
}
