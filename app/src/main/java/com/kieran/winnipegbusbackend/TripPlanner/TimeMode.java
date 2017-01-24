package com.kieran.winnipegbusbackend.TripPlanner;

public enum TimeMode {
    ARRIVE_BEFORE("Arrive Before", "arrive-before"),
    ARRIVE_AFTER("Arrive After", "arrive-after"),
    DEPART_BEFORE("Depart Before", "depart-before"),
    DEPART_AFTER("Depart After", "depart-after");

    public String name;
    public String urlParameter;

    TimeMode(String name, String urlParameter) {
        this.name = name;
        this.urlParameter = urlParameter;
    }
}
