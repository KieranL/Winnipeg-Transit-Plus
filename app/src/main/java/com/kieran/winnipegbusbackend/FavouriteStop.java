package com.kieran.winnipegbusbackend;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class FavouriteStop extends Stop implements Serializable{
    private int timesUsed;
    private transient LatLng latLng; //TODO seperate this somehow?
    private String alias;

    public FavouriteStop(String stopName, int stopNumber, int timesUsed) {
        super(stopName, stopNumber);
        this.timesUsed = timesUsed;
    }

    public FavouriteStop(String stopName, int stopNumber) {
        super(stopName, stopNumber);
        this.timesUsed = 0;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void use() {
        timesUsed++;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        if(alias.equals(stopName))
            this.alias = null;
        else
            this.alias = alias;
    }

    public String getDisplayName() {
        return alias == null ? stopName : alias;
    }
}
