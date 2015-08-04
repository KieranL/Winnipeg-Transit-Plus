package winnipegbusbackend;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private List<RouteSchedule> routeList = new ArrayList<RouteSchedule>();
    private List<Integer> routeNumbers = new ArrayList<Integer>();
    private BusUtilities utilities = new BusUtilities();

    public Stop(Document doc) {
        XMLDocument = doc;
        loadStopName();
        loadRoutes();
    }

    private void loadRouteNumbers() {
        for (RouteSchedule r : routeList)
            routeNumbers.add(r.getRouteNumber());

        Collections.sort(routeNumbers);
    }

    private void loadStopNumber() {
        stopNumber = Integer.parseInt(utilities.getValue(NodeTags.STOP_NUMBER.tag, (Element) XMLDocument.getElementsByTagName(NodeTags.STOP.tag).item(0)));
    }

    private void loadRoutes() {
        NodeList routes = XMLDocument.getElementsByTagName(NodeTags.ROUTES.tag);

        for (int r = 0; r < routes.getLength(); r++) {
            try {
                Node route = routes.item(r);
                routeList.add(new RouteSchedule(route));
            } catch (Exception e) {

            }
        }

        loadRouteNumbers();
    }

    private void refreshRoutes() {
        routeList.clear();
        loadRoutes();
    }

    public void loadStopName() {
        stopName = utilities.getValue(NodeTags.STOP_NAME.tag, (Element) XMLDocument.getElementsByTagName(NodeTags.STOP.tag).item(0));
    }

    public String getName() {
        return stopName;
    }

    public List<RouteSchedule> getRouteList() {
        return routeList;
    }

    public List<Integer> getRouteNumbers() {
        return routeNumbers;
    }

    public List<ScheduledStop> getScheduledStops() {
        List<ScheduledStop> scheduledStops = new ArrayList<ScheduledStop>();

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
                return stop1.getEstimatedDepartureTime().date.compareTo(stop2.getEstimatedDepartureTime().date);
            }
        });

        return scheduledStops;
    }
}
