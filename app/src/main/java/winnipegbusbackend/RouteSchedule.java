package winnipegbusbackend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class RouteSchedule {
    private int routeNumber;
    private String routeName;
    private Node routeNode;
    private List<ScheduledStop> stops = new ArrayList<ScheduledStop>();
    private BusUtilities utilities = new BusUtilities();

    public RouteSchedule(Node node) {
        routeNode = node;

        //loadRouteName();
        loadRouteNumber();
        loadScheduledStops();
    }

    private void loadRouteNumber() {
        routeNumber = Integer.parseInt(utilities.getValue(NodeTags.ROUTE_NUMBER.tag, (Element) routeNode));
    }

    private void loadRouteName() {
        routeName = utilities.getValue(NodeTags.ROUTE_NAME.tag, (Element) routeNode);

    }

    private void loadScheduledStops() {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(NodeTags.SCHEDULED_STOPS.tag);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            stops.add(new ScheduledStop(stop, routeNumber));
        }
    }

    public String getRouteName() {
        return routeName;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public List<ScheduledStop> getScheduledStops() {
        return stops;
    }
}
