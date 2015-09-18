package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbus.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScheduledStop {

    private Node stopNode;
    private ScheduledStopInfo scheduledStopInfo;

    public ScheduledStop(Node stopNode, int routeNumber, int stopNumber, String routeName) {
        scheduledStopInfo = new ScheduledStopInfo();
        scheduledStopInfo.setRouteNumber(routeNumber);
        scheduledStopInfo.setStopNumber(stopNumber);
        this.stopNode = stopNode;
        scheduledStopInfo.setRouteVariantName(routeName);

        loadVariantName();
        loadDepartureTimes();
        scheduledStopInfo.setTimeStatus(getTimeStatus());
    }

    public void loadArrivalTimes() {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.ARRIVAL.tag).item(0);
        try {
            scheduledStopInfo.setEstimatedArrivalTime(BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.ESTIMATED.tag, (Element) arrivalStopNode)));
            scheduledStopInfo.setScheduledArrivalTime(BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.SCHEDULED.tag, (Element) arrivalStopNode)));
            scheduledStopInfo.setHasArrivalTime(true);
        } catch (Exception e) {
            scheduledStopInfo.setHasArrivalTime(false);
        }
    }

    private void loadDepartureTimes() {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.DEPARTURE.tag).item(0);

        scheduledStopInfo.setEstimatedDepartureTime(BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.ESTIMATED.tag, (Element) departureStopNode)));
        scheduledStopInfo.setScheduledDepartureTime(BusUtilities.convertToDate(BusUtilities.getValue(StopTimesNodeTags.SCHEDULED.tag, (Element) departureStopNode)));
    }

    private void loadVariantName() {
        String name = BusUtilities.getValue(StopTimesNodeTags.VARIANT_NAME.tag, (Element) stopNode);
        if(name != null)
            scheduledStopInfo.setRouteVariantName(name);
    }

    public void loadBusInfo() {
        scheduledStopInfo.setHasEasyAccess(Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.EASY_ACCESS.tag, (Element) stopNode)));
        scheduledStopInfo.setHasBikeRack(Boolean.parseBoolean(BusUtilities.getValue(StopTimesNodeTags.BIKE_RACK.tag, (Element) stopNode)));
    }

    public String getRouteVariantName() {
        return scheduledStopInfo.getRouteVariantName();
    }

    public StopTime getScheduledArrivalTime() {
        return scheduledStopInfo.getScheduledArrivalTime();
    }

    public StopTime getScheduledDepartureTime() {
        return scheduledStopInfo.getScheduledDepartureTime();
    }

    public StopTime getEstimatedArrivalTime() {
        return scheduledStopInfo.getEstimatedArrivalTime();
    }

    public StopTime getEstimatedDepartureTime() {
        return scheduledStopInfo.getEstimatedDepartureTime();
    }

    public int getRouteNumber() {
        return scheduledStopInfo.getRouteNumber();
    }

    public int getTimeBehindInMinutes() {
        return StopTime.timeBehindMinutes(scheduledStopInfo.getEstimatedDepartureTime(), scheduledStopInfo.getScheduledDepartureTime());
    }

    public String getTimeStatus() {
        return StopTime.getTimeStatus(scheduledStopInfo.getEstimatedDepartureTime(), scheduledStopInfo.getScheduledDepartureTime());
    }

    public boolean hasArrivalTime() {
        return scheduledStopInfo.hasArrivalTime();
    }

    public ScheduledStopInfo getScheduledStopInfo() {
        return scheduledStopInfo;
    }
}
