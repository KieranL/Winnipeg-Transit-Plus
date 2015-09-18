package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbus.enums.StopTimesNodeTags;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public class RouteSchedule {
    private Node routeNode;
    private int stopNumber;
    private RouteInfo routeInfo;

    public RouteSchedule(Node node, int stopNumber) {
        routeNode = node;
        this.stopNumber = stopNumber;

        routeInfo = new RouteInfo();

        loadRouteName();
        loadRouteNumber();
    }

    private void loadCoverageType() {
        routeInfo.setCoverageType(BusUtilities.getCoverageTypeId(BusUtilities.getValue(StopTimesNodeTags.ROUTE_COVERAGE.tag, (Element) routeNode)));
    }

    private void loadRouteNumber() {
        routeInfo.setRouteNumber(Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.ROUTE_NUMBER.tag, (Element) routeNode)));
    }

    private void loadRouteName() {
        routeInfo.setRouteName(BusUtilities.getValue(StopTimesNodeTags.ROUTE_NAME.tag, (Element) routeNode));
    }

    public void loadScheduledStops() {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(StopTimesNodeTags.SCHEDULED_STOPS.tag);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            routeInfo.getStops().add(new ScheduledStop(stop, routeInfo.getRouteNumber(), stopNumber, routeInfo.getRouteName()));
        }
    }

    public String getRouteName() {
        return routeInfo.getRouteName();
    }

    public int getRouteNumber() {
        return routeInfo.getRouteNumber();
    }

    public List<ScheduledStop> getScheduledStops() {
        return routeInfo.getStops();
    }
}
