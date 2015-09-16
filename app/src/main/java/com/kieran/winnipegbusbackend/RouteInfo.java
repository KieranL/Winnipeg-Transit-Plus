package com.kieran.winnipegbusbackend;

import java.util.ArrayList;
import java.util.List;

public class RouteInfo {
    private int routeNumber;
    private String routeName;
    private List<ScheduledStop> stops;
    private int coverageType;


    public RouteInfo() {
        stops = new ArrayList<>();
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<ScheduledStop> getStops() {
        return stops;
    }

    public void setStops(List<ScheduledStop> stops) {
        this.stops = stops;
    }

    public int getCoverageType() {
        return coverageType;
    }

    public void setCoverageType(int coverageType) {
        this.coverageType = coverageType;
    }
}
