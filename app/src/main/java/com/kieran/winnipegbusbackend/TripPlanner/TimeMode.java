package com.kieran.winnipegbusbackend.TripPlanner;

import java.io.Serializable;

public enum TimeMode implements Serializable {
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

    public static TimeMode getTimeModeByName(String name) {
        TimeMode[] modes = {ARRIVE_BEFORE, ARRIVE_AFTER, DEPART_BEFORE, DEPART_AFTER};

        for(TimeMode m : modes) {
            if(m.name.equals(name))
                return m;
        }

        return DEPART_AFTER;
    }

}
