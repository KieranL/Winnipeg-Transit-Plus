package com.kieran.winnipegbusbackend;

import org.w3c.dom.Node;

import java.io.Serializable;

public class RouteKey implements Serializable {
    private final static String ROUTE_KEY_TAG = "key";
    private int routeNumber;
    private int routeDirection;
    private char variant = 0;

    public RouteKey(Node node) {
        String[] elements = BusUtilities.getValue(ROUTE_KEY_TAG, node).split("-");
        routeNumber = Integer.parseInt(elements[0]);
        routeDirection = Integer.parseInt(elements[1]);

        if(elements.length > 2)
            variant = elements[2].charAt(0);
    }

    public String getKeyString() {
        String key = routeNumber + "-" + routeDirection;

        if(variant != 0)
            key += "-" + variant;

        return key;
    }
}