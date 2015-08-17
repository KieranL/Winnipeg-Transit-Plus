package winnipegbusbackend;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ScheduledStop {

    private Node stopNode;
    private ScheduledStopInfo scheduledStopInfo;

    private BusUtilities utilities = new BusUtilities();

    public ScheduledStop(Node stopNode, int routeNumber, int stopNumber) {
        scheduledStopInfo = new ScheduledStopInfo();
        scheduledStopInfo.setRouteNumber(routeNumber);
        scheduledStopInfo.setStopNumber(stopNumber);
        this.stopNode = stopNode;

        loadVariantName();
        loadTimes();
        loadBusInfo();
        scheduledStopInfo.setTimeStatus(getTimeStatus());
    }

    private void loadTimes() {
        loadArrivalTimes();
        loadDepartureTimes();
    }

    private void loadArrivalTimes() {
        Node arrivalStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.ARRIVAL.tag).item(0);
        try {
            scheduledStopInfo.setEstimatedArrivalTime(utilities.convertToDate(utilities.getValue(StopTimesNodeTags.ESTIMATED.tag, (Element) arrivalStopNode)));
            scheduledStopInfo.setScheduledArrivalTime(utilities.convertToDate(utilities.getValue(StopTimesNodeTags.SCHEDULED.tag, (Element) arrivalStopNode)));
            scheduledStopInfo.setHasArrivalTime(true);
        } catch (Exception e) {
            scheduledStopInfo.setHasArrivalTime(false);
        }
    }

    private void loadDepartureTimes() {
        Node departureStopNode = ((Element) stopNode).getElementsByTagName(StopTimesNodeTags.DEPARTURE.tag).item(0);

        scheduledStopInfo.setEstimatedDepartureTime(utilities.convertToDate(utilities.getValue(StopTimesNodeTags.ESTIMATED.tag, (Element) departureStopNode)));
        scheduledStopInfo.setScheduledDepartureTime(utilities.convertToDate(utilities.getValue(StopTimesNodeTags.SCHEDULED.tag, (Element) departureStopNode)));
    }

    private void loadVariantName() {
        scheduledStopInfo.setRouteVariantName(utilities.getValue(StopTimesNodeTags.VARIANT_NAME.tag, (Element) stopNode));
    }

    public void loadBusInfo() {
        scheduledStopInfo.setHasEasyAccess(Boolean.parseBoolean(utilities.getValue(StopTimesNodeTags.EASY_ACCESS.tag, (Element) stopNode)));
        scheduledStopInfo.setHasBikeRack(Boolean.parseBoolean(utilities.getValue(StopTimesNodeTags.BIKE_RACK.tag, (Element) stopNode)));
    }

    public String getRouteVariantName() {
        return scheduledStopInfo.getRouteVariantName();
    }

    public StopTime getScheduledArrivalTime() {
        return scheduledStopInfo.getScheduledArrivalTime();
    }

    public StopTime getScheduledDepartureTime() {
        return scheduledStopInfo.getScheduledDepartureTime();
    }

    public StopTime getEstimatedArrivalTime() {
        return scheduledStopInfo.getEstimatedArrivalTime();
    }

    public StopTime getEstimatedDepartureTime() {
        return scheduledStopInfo.getEstimatedDepartureTime();
    }

    public int getRouteNumber() {
        return scheduledStopInfo.getRouteNumber();
    }

    public int getTimeBehindInMinutes() {
        return StopTime.timeBehindMinutes(scheduledStopInfo.getEstimatedDepartureTime(), scheduledStopInfo.getScheduledDepartureTime());
    }

    public String getTimeStatus() {
        return StopTime.getTimeStatus(scheduledStopInfo.getEstimatedDepartureTime(), scheduledStopInfo.getScheduledDepartureTime());
    }

    public boolean hasArrivalTime() {
        return scheduledStopInfo.hasArrivalTime();
    }

    public ScheduledStopInfo getScheduledStopInfo() {
        return scheduledStopInfo;
    }
}
