package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.TimeStatuses;

import java.util.Date;

public class StopTime implements Comparable {
    private byte hours;
    private byte minutes;
    private long milliseconds;

    public StopTime(Date date) {
        hours = (byte)date.getHours();
        minutes = (byte)date.getMinutes();
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

    public void setHours(byte hours) {
        this.hours = hours;
    }

    public void increaseHour(byte increase) {
        hours += increase;
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

    public long getMilliseconds() {
        return milliseconds;
    }

    public String to12hrTimeString() {
        String timeString;

        if(hours == 0)
            timeString = "12" + ":" + getMinutes();
        else if(hours <= 12)
            timeString = getHours() + ":" + getMinutes();
        else
            timeString = (hours - 12) + ":" + getMinutes();

        timeString += (hours >= 12) ? "p" : "a";

        return timeString;
    }

    public String toFormattedString(StopTime currentTime, boolean use24hrTime) {
        if(currentTime != null) {
            int remainingTime = timeBehindMinutes(this, currentTime);

            if (remainingTime <= 1)
                return "Due";
            else if (remainingTime <= 15)
                return remainingTime + " min";
        }

        if(use24hrTime)
            return this.to24hrTimeString();
        else
            return this.to12hrTimeString();
    }

    public String to24hrTimeString() {
        return ((hours < 10) ? "0" + getHours() : getHours()) + ":" + getMinutes();
    }

    @Override
    public int compareTo(Object another) {
        return (int)(milliseconds - ((StopTime)another).getMilliseconds());
    }
}
