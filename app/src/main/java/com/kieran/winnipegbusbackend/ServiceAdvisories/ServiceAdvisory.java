package com.kieran.winnipegbusbackend.ServiceAdvisories;

import com.kieran.winnipegbusbackend.StopTime;

import java.io.Serializable;
import java.util.List;

public class ServiceAdvisory implements Serializable, Comparable {
    private String title;
    private String header;
    private List<AffectedStop> affectedStops;
    private List<Reroute> reroutes;
    private StopTime updatedAt;

    public ServiceAdvisory(String title, String header, List<AffectedStop> affectedStops, List<Reroute> reroutes, StopTime updatedAt) {
        this.title = title;
        this.header = header;
        this.affectedStops = affectedStops;
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

    public String getHeader() {
        return header;
    }

    public List<AffectedStop> getAffectedStops() {
        return affectedStops;
    }

    @Override
    public int compareTo(Object another) {
        return -updatedAt.compareTo(((ServiceAdvisory)another).getUpdatedAt());
    }
}
