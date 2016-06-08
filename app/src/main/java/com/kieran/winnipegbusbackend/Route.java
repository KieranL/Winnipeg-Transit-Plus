package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import org.w3c.dom.Node;

import java.io.Serializable;

public class Route implements Comparable, Serializable {
    private final static String ROUTE_COVERAGE_TAG = "coverage";
    private final static String ROUTE_NUMBER_TAG = "key";
    private final static String ROUTE_NAME_TAG = "name";
    public static final int DT_SPIRIT_MAX_RT_NUM = 10;

    protected int routeNumber;
    protected String routeName;
    protected CoverageTypes coverageType;

    public Route(Node routeNode) {
        coverageType = CoverageTypes.getEnum(BusUtilities.getValue(ROUTE_COVERAGE_TAG, routeNode));
        routeName = BusUtilities.getValue(ROUTE_NAME_TAG, routeNode);
        routeNumber = Integer.parseInt(BusUtilities.getValue(ROUTE_NUMBER_TAG, routeNode));
    }

    public Route(Route route) {
        routeNumber = route.getRouteNumber();
        routeName =  route.getRouteName();
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
        return routeNumber - ((Route)another).getRouteNumber();
    }

    public boolean isDownTownSpirit() {
        return isDownTownSpirit(routeNumber);
    }

    public static boolean isDownTownSpirit(int routeNumber) {
        return routeNumber < DT_SPIRIT_MAX_RT_NUM;
    }
}
