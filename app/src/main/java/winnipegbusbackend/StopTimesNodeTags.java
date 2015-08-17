package winnipegbusbackend;

public enum StopTimesNodeTags {
    BIKE_RACK("bike-rack"),
    EASY_ACCESS("easy-access"),
    ARRIVAL("arrival"),
    DEPARTURE("departure"),
    SCHEDULED("scheduled"),
    ESTIMATED("estimated"),
    VARIANT_NAME("name"),
    SCHEDULED_STOPS("scheduled-stop"),
    ROUTE_NAME("name"),
    ROUTES("route-schedule"),
    ROUTE_NUMBER("key"),
    STOP_NAME("name"),
    STOP_NUMBER("number"),
    STOP("stop");

    public String tag;

    StopTimesNodeTags(String tag) {
        this.tag = tag;
    }

}
