package com.kieran.winnipegbusbackend;

import java.io.Serializable;

public class RouteKey implements Serializable {
    private final static String ROUTE_KEY_TAG = "key";
    private int routeNumber;
    private int routeDirection;
    private char variant = 0;

    public RouteKey(String key) {
        String[] elements = key.split("-");
        routeNumber = Integer.parseInt(elements[0]);
        routeDirection = Integer.parseInt(elements[1]);

        if(elements.length > 2)
            variant = elements[2].charAt(0);
    }

    public RouteKey(int routeNumber, int routeDirection, char variant) {
        this.routeNumber = routeNumber;
        this.routeDirection = routeDirection;
        this.variant = variant;
    }

    public String getKeyString() {
        String key = routeNumber + "-" + routeDirection;

        if(variant != 0)
            key += "-" + variant;

        return key;
    }
}