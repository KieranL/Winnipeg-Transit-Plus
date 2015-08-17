package winnipegbusbackend;

public enum FavouritesNodeTags {
    FAVOURITE_STOPS("favouriteStops"),
    FAVOURITE_STOP("favouriteStop"),
    STOP_NUMBER("stopNumber"),
    STOP_NAME("stopName"),
    TIMES_USED("timesUsed");

    public String tag;

    FavouritesNodeTags(String tag) {
        this.tag = tag;
    }
}
