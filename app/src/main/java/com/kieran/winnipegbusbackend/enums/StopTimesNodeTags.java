package com.kieran.winnipegbusbackend.enums;

public enum StopTimesNodeTags {
    BIKE_RACK("bike-rack"),
    EASY_ACCESS("easy-access"),
    KEY("key"), //TODO separate for route key and stop key
    VARIANT_NAME("name"),
    SCHEDULED_STOPS("scheduled-stop"),
    ROUTE_NAME("name"),
    ROUTES("route-schedule"),
    ROUTE_NUMBER("key"),
    ROUTE_COVERAGE("coverage"),
    STOP_NAME("name"), //used in searchresults and stopschedule
    STOP_NUMBER("number"), //used in searchresults and stopschedule
    VARIANT("variant"),
    STOP("stop"), //used in stopschedule and scheduledstopinfo
    GEOGRAPHIC("geographic"),
    LATITUDE("latitude"), //used in searchresults and stopschedule
    LONGITUDE("longitude"); //used in searchresults and stopschedule

    public String tag;

    StopTimesNodeTags(String tag) {
        this.tag = tag;
    }

}