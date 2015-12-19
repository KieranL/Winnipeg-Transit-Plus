package com.kieran.winnipegbusbackend;


import com.kieran.winnipegbusbackend.enums.StopTimesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Stop {
    private Document XMLDocument;
    private String stopName;
    private int stopNumber;
    private List<RouteSchedule> routeList = new ArrayList<>();

    public Stop(Document document, int stopNumber) {
        XMLDocument = document;
        this.stopNumber = stopNumber;
        loadStopName();
    }

    public Stop(Document document) {
        XMLDocument = document;
        loadStopNumber();
        loadStopName();
    }

    private void loadStopNumber() {
        stopNumber = Integer.parseInt(BusUtilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, XMLDocument.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0)));
    }

    public Stop loadRoutes() {
        NodeList routes = XMLDocument.getElementsByTagName(StopTimesNodeTags.ROUTES.tag);

        for (int r = 0; r < routes.getLength(); r++)
                routeList.add(new RouteSchedule(routes.item(r), stopNumber));

        return this;
    }

    public void loadStopName() {
        stopName = BusUtilities.getValue(StopTimesNodeTags.STOP_NAME.tag,  XMLDocument.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0));
    }

    public String getName() {
        return stopName;
    }

    public List<RouteSchedule> getRouteList() {
        return routeList;
    }

    public int getStopNumber() {
        return stopNumber;
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
        XMLDocument = document;
        loadRoutes();
    }

    public ScheduledStop getScheduledStopByKey(ScheduledStopKey key) {
        for (ScheduledStop scheduledStop : getScheduledStops())
            if(scheduledStop.getKey().getBusKey() == key.getBusKey())
                return scheduledStop;
        return null;
    }

    public StopFeatures createStopFeatures() {
        return new StopFeatures(stopNumber, stopName, getGPSCoordinate());
    }

    private GPSCoordinate getGPSCoordinate() {
        Node coordinates = XMLDocument.getElementsByTagName(StopTimesNodeTags.GEOGRAPHIC.tag).item(0);
        return new GPSCoordinate(BusUtilities.getValue(StopTimesNodeTags.LATITUDE.tag, coordinates), BusUtilities.getValue(StopTimesNodeTags.LONGITUDE.tag, coordinates));
    }
}
