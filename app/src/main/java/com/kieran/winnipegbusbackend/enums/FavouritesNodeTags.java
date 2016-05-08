package com.kieran.winnipegbusbackend.enums;

public enum FavouritesNodeTags {
    FAVOURITE_STOPS("favouriteStops"),
    FAVOURITE_STOP("favouriteStop"),
    STOP_NUMBER("stopNumber"),
    STOP_NAME("stopName"),
    TIMES_USED("timesUsed"),
    ALIAS("alias");

    public String tag;

    FavouritesNodeTags(String tag) {
        this.tag = tag;
    }
}
