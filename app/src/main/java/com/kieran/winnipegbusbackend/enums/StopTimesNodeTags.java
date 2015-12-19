package com.kieran.winnipegbusbackend.enums;

public enum StopTimesNodeTags {
    BIKE_RACK("bike-rack"),
    EASY_ACCESS("easy-access"),
    SCHEDULED_STOP_KEY("key"),
    VARIANT_NAME("name"),
    SCHEDULED_STOPS("scheduled-stop"),
    ROUTE_NAME("name"),
    ROUTES("route-schedule"),
    ROUTE_NUMBER("key"),
    ROUTE_COVERAGE("coverage"),
    STOP_NAME("name"),
    STOP_NUMBER("number"),
    STOP("stop"),
    GEOGRAPHIC("geographic"),
    LATITUDE("latitude"),
    LONGITUDE("longitude");

    public String tag;

    StopTimesNodeTags(String tag) {
        this.tag = tag;
    }

}
