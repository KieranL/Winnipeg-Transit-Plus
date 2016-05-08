package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Node;

import java.io.Serializable;

public class RouteKey implements Serializable {
    private int routeNumber;
    private int routeDirection;
    private char variant;

    public RouteKey(Node node) {
        String[] elements = BusUtilities.getValue(StopTimesNodeTags.KEY.tag, node).split("-");
        routeNumber = Integer.parseInt(elements[0]);
        routeDirection = Integer.parseInt(elements[1]);
        variant = elements[2].charAt(0);
    }

    public String getKeyString() {
        return routeNumber + "-" + routeDirection + "-" + variant;
    }
}