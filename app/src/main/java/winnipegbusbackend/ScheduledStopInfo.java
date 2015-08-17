package winnipegbusbackend;

public class ScheduledStopInfo {
    private String routeVariantName;
    private StopTime estimatedArrivalTime;
    private StopTime estimatedDepartureTime;
    private StopTime scheduledArrivalTime;
    private StopTime scheduledDepartureTime;
    private int routeNumber;
    private int stopNumber;
    private boolean hasBikeRack;
    private boolean hasEasyAccess;
    private boolean hasArrivalTime;
    private String timeStatus;

    public ScheduledStopInfo() {

    }

    public String getRouteVariantName() {
        return routeVariantName;
    }

    public void setRouteVariantName(String routeVariantName) {
        this.routeVariantName = routeVariantName;
    }

    public StopTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(StopTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public StopTime getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }

    public void setEstimatedDepartureTime(StopTime estimatedDepartureTime) {
        this.estimatedDepartureTime = estimatedDepartureTime;
    }

    public StopTime getScheduledArrivalTime() {
        return scheduledArrivalTime;
    }

    public void setScheduledArrivalTime(StopTime scheduledArrivalTime) {
        this.scheduledArrivalTime = scheduledArrivalTime;
    }

    public StopTime getScheduledDepartureTime() {
        return scheduledDepartureTime;
    }

    public void setScheduledDepartureTime(StopTime scheduledDepartureTime) {
        this.scheduledDepartureTime = scheduledDepartureTime;
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(int routeNumber) {
        this.routeNumber = routeNumber;
    }

    public boolean hasBikeRack() {
        return hasBikeRack;
    }

    public void setHasBikeRack(boolean hasBikeRack) {
        this.hasBikeRack = hasBikeRack;
    }

    public boolean hasEasyAccess() {
        return hasEasyAccess;
    }

    public void setHasEasyAccess(boolean hasEasyAccess) {
        this.hasEasyAccess = hasEasyAccess;
    }

    public boolean hasArrivalTime() {
        return hasArrivalTime;
    }

    public void setHasArrivalTime(boolean hasArrivalTime) {
        this.hasArrivalTime = hasArrivalTime;
    }

    public String getTimeStatus() {
        return timeStatus;
    }

    public void setTimeStatus(String timeStatus) {
        this.timeStatus = timeStatus;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public void setStopNumber(int stopNumber) {
        this.stopNumber = stopNumber;
    }
}
