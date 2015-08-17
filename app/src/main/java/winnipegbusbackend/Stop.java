package winnipegbusbackend;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
    private List<Integer> routeNumbers = new ArrayList<>();
    private BusUtilities utilities = new BusUtilities();

    public Stop(Document doc, int stopNumber) {
        XMLDocument = doc;
        this.stopNumber = stopNumber;
        loadStopName();
        loadRoutes();
    }

    private void loadRouteNumbers() {
        for (RouteSchedule r : routeList)
            routeNumbers.add(r.getRouteNumber());

        Collections.sort(routeNumbers);
    }

    private void loadStopNumber() {
        stopNumber = Integer.parseInt(utilities.getValue(StopTimesNodeTags.STOP_NUMBER.tag, (Element) XMLDocument.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0)));
    }

    private void loadRoutes() {
        NodeList routes = XMLDocument.getElementsByTagName(StopTimesNodeTags.ROUTES.tag);

        for (int r = 0; r < routes.getLength(); r++)
                routeList.add(new RouteSchedule(routes.item(r), stopNumber));

        loadRouteNumbers();
    }

    public void loadStopName() {
        stopName = utilities.getValue(StopTimesNodeTags.STOP_NAME.tag, (Element) XMLDocument.getElementsByTagName(StopTimesNodeTags.STOP.tag).item(0));
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
                return stop1.getEstimatedDepartureTime().date.compareTo(stop2.getEstimatedDepartureTime().date);
            }
        });

        return scheduledStops;
    }

    public List<ScheduledStopInfo> getScheduledStopInfosSorted() {
        List<ScheduledStop> stops = getScheduledStopsSorted();
        List<ScheduledStopInfo> stopInfos = new ArrayList<>();

        for(ScheduledStop stopInfo : stops) {
            stopInfos.add(stopInfo.getScheduledStopInfo());
        }

        return stopInfos;
    }
}
