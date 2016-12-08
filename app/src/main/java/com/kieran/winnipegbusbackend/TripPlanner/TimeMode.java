package com.kieran.winnipegbusbackend.TripPlanner;

enum TimeMode {
    ARRIVE_BEFORE("Arrive Before"),
    ARRIVE_AFTER("Arrive After"),
    DEPART_BEFORE("Depart Before"),
    DEPART_AFTER("Depart After");

    String name;

    TimeMode(String name) {
        this.name = name;
    }
}
