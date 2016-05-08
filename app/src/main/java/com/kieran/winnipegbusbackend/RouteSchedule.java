package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteSchedule implements Comparable, Serializable {
    private int routeNumber;
    private String routeName;
    private CoverageTypes coverageType;
    private List<ScheduledStop> stops;

    public RouteSchedule(Node node) {
        stops = new ArrayList<>();

        loadCoverageType(node);
        loadRouteName(node);
        loadRouteNumber(node);
        loadScheduledStops(node);
    }

    public RouteSchedule(RouteSchedule routeSchedule) {
        routeNumber = routeSchedule.getRouteNumber();
        routeName = routeSchedule.getRouteName();
    }

    private void loadCoverageType(Node routeNode) {
       coverageType = CoverageTypes.getEnum(BusUtilities.getValue(StopTimesNodeTags.ROUTE_COVERAGE.tag, routeNode));
    }

    private void loadRouteNumber(Node routeNode) {
        routeNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.ROUTE_NUMBER.tag, routeNode));
    }

    private void loadRouteName(Node routeNode) {
        routeName = BusUtilities.getValue(StopTimesNodeTags.ROUTE_NAME.tag, routeNode);
    }

    public void loadScheduledStops(Node routeNode) {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(StopTimesNodeTags.SCHEDULED_STOPS.tag);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            stops.add(new ScheduledStop(stop, this));
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

    public CoverageTypes getCoverageType() {
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
