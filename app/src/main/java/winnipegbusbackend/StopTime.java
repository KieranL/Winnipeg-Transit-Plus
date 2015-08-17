package winnipegbusbackend;

import java.util.Date;

public class StopTime {
    Date date;
    int hours;
    int minutes;
    int seconds;
    long milliseconds;

    public StopTime(Date _date) {
        date = _date;
        hours = date.getHours();
        minutes = date.getMinutes();
        seconds = date.getSeconds();
        milliseconds = date.getTime();
    }

    public static int timeBehindMinutes(StopTime estimated, StopTime scheduled) {
        return Math.round(((estimated.milliseconds - scheduled.milliseconds) / 1000 / 60));
    }

    public static String getTimeStatus(StopTime estimated, StopTime scheduled) {
        int timeStatusNum = timeBehindMinutes(estimated, scheduled);

        if (timeStatusNum == 0)
            return TimeStatuses.Ok.status;
        else if (timeStatusNum < 0)
            return TimeStatuses.Early.status;
        else
            return TimeStatuses.Late.status;
    }

    public String getMinutes() {
        return (minutes >= 10) ? String.valueOf(minutes) : ("0" + String.valueOf(minutes));
    }

    public String getHours() {
        return String.valueOf(hours);
    }

    public String toString() {
        return getHours() + ":" + getMinutes();
    }

}
