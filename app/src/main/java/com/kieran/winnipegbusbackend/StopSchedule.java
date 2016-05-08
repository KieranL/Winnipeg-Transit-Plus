package com.kieran.winnipegbusbackend;


import com.google.android.gms.maps.model.LatLng;
import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StopSchedule extends Stop {
    private List<RouteSchedule> routeList = new ArrayList<>();
    private LatLng latLng;

    public StopSchedule(Document document, int stopNumber) {
        super(stopNumber);
        loadStopName(document);
        loadRoutes(document);
        loadLatLng(document);
    }

    public StopSchedule(Document document) {
        loadStopNumber(document);
        loadStopName(document);
        loadRoutes(document);
        loadLatLng(document);
    }

    private void loadStopNumber(Document document) {
        stopNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, document.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0)));
    }

    public StopSchedule loadRoutes(Document document) {
        NodeList routes = document.getElementsByTagName(StopTimesNodeTags.ROUTES.tag);

        for (int r = 0; r < routes.getLength(); r++)
                routeList.add(new RouteSchedule(routes.item(r)));

        return this;
    }

    public void loadStopName(Document document) {
        stopName = BusUtilities.getValue(StopTimesNodeTags.STOP_NAME.tag,  document.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0));
    }

    public void loadLatLng(Document document) {
        Node coordinates = document.getElementsByTagName(StopTimesNodeTags.GEOGRAPHIC.tag).item(0);
        latLng = new LatLng(Double.parseDouble(BusUtilities.getValue(StopTimesNodeTags.LATITUDE.tag, coordinates)), Double.parseDouble(BusUtilities.getValue(StopTimesNodeTags.LONGITUDE.tag, coordinates)));
    }

    public List<RouteSchedule> getRouteList() {
        return routeList;
    }

    public List<ScheduledStop> getScheduledStops() {
        List<ScheduledStop> scheduledStops = new ArrayList<>();

        for (RouteSchedule r : routeList)
            for (ScheduledStop s : r.getScheduledStops())
                scheduledStops.add(s);

        return scheduledStops;
    }

    public List<ScheduledStop> getScheduledStopsSorted() {
        List<ScheduledStop> scheduledStops = getScheduledStops();

        Collections.sort(scheduledStops, new Comparator<ScheduledStop>() {
            @Override
            public int compare(ScheduledStop stop1, ScheduledStop stop2) {
                return stop1.getEstimatedDepartureTime().compareTo(stop2.getEstimatedDepartureTime());
            }
        });

        return scheduledStops;
    }

    public void refresh(Document document) {
        routeList.clear();
        loadRoutes(document);
    }

    public ScheduledStop getScheduledStopByKey(ScheduledStopKey key) {
        for (ScheduledStop scheduledStop : getScheduledStops())
            if(scheduledStop.getKey().equals(key))
                return scheduledStop;
        return null;
    }

    public StopFeatures createStopFeatures() {
        return new StopFeatures(stopNumber, stopName, getLatLng());
    }

    private LatLng getLatLng() {
        return latLng;
    }
}
