package winnipegbusbackend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

public class RouteSchedule {
    private Node routeNode;
    private int stopNumber;
    private BusUtilities utilities;
    private RouteInfo routeInfo;

    public RouteSchedule(Node node, int stopNumber) {
        routeNode = node;
        this.stopNumber = stopNumber;

        utilities = new BusUtilities();
        routeInfo = new RouteInfo();

        loadRouteName();
        loadRouteNumber();
        loadScheduledStops();
    }

    private void loadRouteNumber() {
        routeInfo.setRouteNumber(Integer.parseInt(utilities.getValue(StopTimesNodeTags.ROUTE_NUMBER.tag, (Element) routeNode)));
    }

    private void loadRouteName() {
        routeInfo.setRouteName(utilities.getValue(StopTimesNodeTags.ROUTE_NAME.tag, (Element) routeNode));
    }

    private void loadScheduledStops() {
        NodeList scheduledStops = ((Element) routeNode).getElementsByTagName(StopTimesNodeTags.SCHEDULED_STOPS.tag);

        for (int s = 0; s < scheduledStops.getLength(); s++) {
            Node stop = scheduledStops.item(s);
            routeInfo.getStops().add(new ScheduledStop(stop, routeInfo.getRouteNumber(), stopNumber));
        }
    }

    public String getRouteName() {
        return routeInfo.getRouteName();
    }

    public int getRouteNumber() {
        return routeInfo.getRouteNumber();
    }

    public List<ScheduledStop> getScheduledStops() {
        return routeInfo.getStops();
    }
}
