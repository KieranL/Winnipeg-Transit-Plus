package com.kieran.winnipegbusbackend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteSchedule extends Route implements Serializable {
    private final static String SCHEDULED_STOPS_TAG = "scheduled-stop";

    private List<ScheduledStop> stops;

    public RouteSchedule(Node node) {
        super(node);
        stops = new ArrayList<>();

        loadScheduledStops(node);
    }

    public RouteSchedule(RouteSchedule routeSchedule) {
        super(routeSchedule);
    }

    public void loadScheduledStops(Node routeNode) {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(SCHEDULED_STOPS_TAG);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            try {
                stops.add(new ScheduledStop(stop, this));
            }catch (Exception e) {
                //blank
            }

        }
    }

    public List<ScheduledStop> getScheduledStops() {
        return stops;
    }
}
