package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class RouteSchedule implements Comparable{
    private Node routeNode;
    private int stopNumber;


    private int routeNumber;
    private String routeName;
    private int coverageType;

    private List<ScheduledStop> stops;

    public RouteSchedule(Node node, int stopNumber) {
        routeNode = node;
        this.stopNumber = stopNumber;
        stops = new ArrayList<>();

        loadRouteName();
        loadRouteNumber();
        loadScheduledStops();
    }

    public RouteSchedule(RouteSchedule routeSchedule) {
        routeNumber = routeSchedule.getRouteNumber();
        routeName = routeSchedule.getRouteName();
    }

    public void loadCoverageType() {
       coverageType = BusUtilities.getCoverageTypeId(BusUtilities.getValue(StopTimesNodeTags.ROUTE_COVERAGE.tag, routeNode));
    }

    private void loadRouteNumber() {
        routeNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.ROUTE_NUMBER.tag, routeNode));
    }

    private void loadRouteName() {
        routeName = BusUtilities.getValue(StopTimesNodeTags.ROUTE_NAME.tag, routeNode);
    }

    public void loadScheduledStops() {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(StopTimesNodeTags.SCHEDULED_STOPS.tag);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            stops.add(new ScheduledStop(stop, routeNumber, routeName));
        }
    }

    public List<ScheduledStop> getScheduledStops() {
        return stops;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public int getCoverageType() {
        return coverageType;
    }

    public String toString(){
        return routeNumber + " " + ((routeName != null) ? routeName : "");
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return routeNumber - ((RouteSchedule)another).getRouteNumber();
    }
}
