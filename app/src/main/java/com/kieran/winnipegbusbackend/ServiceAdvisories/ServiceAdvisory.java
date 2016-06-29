package com.kieran.winnipegbusbackend.ServiceAdvisories;

import com.kieran.winnipegbusbackend.StopTime;

import java.util.List;

public class ServiceAdvisory {
    private String title;
    private String header;
    private List<String> affectedStops;
    private List<Reroute> reroutes;
    private StopTime updatedAt;

    public ServiceAdvisory(String title, List<Reroute> reroutes, StopTime updatedAt) {
        this.title = title;
        this.reroutes = reroutes;
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public List<Reroute> getReroutes() {
        return reroutes;
    }

    public StopTime getUpdatedAt() {
        return updatedAt;
    }
}
