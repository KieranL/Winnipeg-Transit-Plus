package winnipegbusbackend;

public class FavouriteStop {
    private String stopName;
    private int stopNumber;
    private int timesUsed;

    public FavouriteStop(String stopName, int stopNumber, int timesUsed) {
        this.stopName = stopName;
        this.stopNumber = stopNumber;
        this.timesUsed = timesUsed;
    }

    public FavouriteStop(String stopName, int stopNumber) {
        this.stopName = stopName;
        this.stopNumber = stopNumber;
        this.timesUsed = 0;
    }

    public static FavouriteStop clone(FavouriteStop favouriteStop) {
        return new FavouriteStop(favouriteStop.getStopName(), favouriteStop.getStopNumber(), favouriteStop.getTimesUsed());
    }

    public String getStopName() {
        return stopName;
    }

    public int getStopNumber() {
        return stopNumber;
    }

    public int getTimesUsed() {
        return timesUsed;
    }

    public void use() {
        timesUsed++;
    }
}
