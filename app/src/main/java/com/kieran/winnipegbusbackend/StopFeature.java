package com.kieran.winnipegbusbackend;

import org.w3c.dom.Node;

import java.io.Serializable;

public class StopFeature implements Serializable {
    private static final String NAME_TAG = "name";
    private static final String COUNT_TAG = "count";
    private int count;
    private String name;

    public StopFeature(int count, String name) {
        this.count = count;
        this.name = name;
    }

    public StopFeature(Node featureNode) {
        name = BusUtilities.getValue(NAME_TAG, featureNode);
        count = Integer.parseInt(BusUtilities.getValue(COUNT_TAG, featureNode));
    }

    public int getCount() {
        return count;
    }

    public String getName() {
        return name;
    }
}
