package winnipegbusbackend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.sql.Time;
import java.util.Date;

public class ScheduledStop {

    private String routeVariantName;
    private Node stopNode;
    private StopTime estimatedArrivalTime;
    private StopTime estimatedDepartureTime;
    private StopTime scheduledArrivalTime;
    private StopTime scheduledDepartureTime;
    private int routeNumber;
    private boolean hasBikeRack;
    private boolean hasEasyAccess;
    private boolean hasArrivalTime;
    private BusUtilities utilities = new BusUtilities();

    public ScheduledStop(Node stopNode, int routeNumber) {
        this.stopNode = stopNode;
        this.routeNumber = routeNumber;
        loadVariantName();
        loadTimes();
        loadBusInfo();
    }

    private void loadTimes() {
        loadArrivalTimes();
        loadDepartureTimes();
    }

    private void loadArrivalTimes() {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(NodeTags.ARRIVAL.tag).item(0);
        try {
            estimatedArrivalTime = utilities.convertToDate(utilities.getValue(NodeTags.ESTIMATED.tag, (Element) arrivalStopNode));
            scheduledArrivalTime = utilities.convertToDate(utilities.getValue(NodeTags.SCHEDULED.tag, (Element) arrivalStopNode));
            hasArrivalTime = true;
        } catch (Exception e) {
            hasArrivalTime = false;
        }

    }

    private void loadDepartureTimes() {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(NodeTags.DEPARTURE.tag).item(0);

        estimatedDepartureTime = utilities.convertToDate(utilities.getValue(NodeTags.ESTIMATED.tag, (Element) departureStopNode));
        scheduledDepartureTime = utilities.convertToDate(utilities.getValue(NodeTags.SCHEDULED.tag, (Element) departureStopNode));
    }

    private void loadVariantName() {
        routeVariantName = utilities.getValue(NodeTags.VARIANT_NAME.tag, (Element) stopNode);
    }

    public void loadBusInfo() {
        hasEasyAccess = Boolean.parseBoolean(utilities.getValue(NodeTags.EASY_ACCESS.tag, (Element) stopNode));
        hasBikeRack = Boolean.parseBoolean(utilities.getValue(NodeTags.BIKE_RACK.tag, (Element) stopNode));
    }

    public String getRouteVariantName() {
        return routeVariantName;
    }

    public StopTime getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public StopTime getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public StopTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public StopTime getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public boolean hasEasyAccess() {
        return hasEasyAccess;
    }

    public boolean hasBikeRack() {
        return hasBikeRack;
    }

    public int getTimeBehindInMinutes() {
        return StopTime.timeBehindMinutes(estimatedDepartureTime, scheduledDepartureTime);
    }

    public String getTimeStatus() {
        return StopTime.getTimeStatus(estimatedDepartureTime, scheduledDepartureTime);
    }

    public String toString() {
        return estimatedDepartureTime.getHours() + ":" + estimatedDepartureTime.getMinutes();
    }

    public boolean hasArrivalTime() {
        return hasArrivalTime;
    }
}
