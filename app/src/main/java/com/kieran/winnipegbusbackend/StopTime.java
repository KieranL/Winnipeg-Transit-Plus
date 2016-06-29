package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StopTime implements Comparable, Serializable {
    private static final double DUE_TIME = 0.5;
    private static final String DUE_STRING = "Due";
    private static final double EARLY_TIME = -0.5;
    private static final double LATE_TIME = 0.5;
    private static final String EARLY_TEXT = "Early";
    private static final String LATE_TEXT = "Late";
    private static final String OK_TEXT = "Ok";
    private static transient Calendar calendar = Calendar.getInstance();
    private long milliseconds;

    public StopTime() {
        milliseconds = System.currentTimeMillis();
    }

    public StopTime(Date date) {
        milliseconds = date.getTime();
    }

    public StopTime(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    public static double timeBehindMinutes(StopTime estimated, StopTime scheduled) {
        return (estimated.milliseconds - scheduled.milliseconds) / 1000 / 60;
    }

    public static String getTimeStatus(StopTime estimated, StopTime scheduled) {
        double timeStatusNum = timeBehindMinutes(estimated, scheduled);

        if (timeStatusNum < LATE_TIME && timeStatusNum > EARLY_TIME)
            return OK_TEXT;
        else if (timeStatusNum <= EARLY_TIME)
            return EARLY_TEXT;
        else
            return LATE_TEXT;
    }

    public void increaseHour(int increase) {
        milliseconds += increase * 1000 * 60 * 60;
    }

    public String getMinutesString() {
        int minutes = getMinutes();
        return (minutes >= 10) ? String.valueOf(minutes) : ("0" + String.valueOf(minutes));
    }

    public String getHoursString() {
        return Integer.toString(getHours());
    }

    public String toString() {
        return getHoursString() + ":" + getMinutesString();
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public String to12hrTimeString() {
        String timeString;
        int hours = getHours();

        if(hours == 0)
            timeString = "12" + ":" + getMinutesString();
        else if(hours <= 12)
            timeString = getHoursString() + ":" + getMinutesString();
        else
            timeString = (hours - 12) + ":" + getMinutesString();

        timeString += (hours >= 12) ? "p" : "a";

        return timeString;
    }

    public String toFormattedString(StopTime currentTime, boolean use24hrTime) {
        if(currentTime != null) {
            double remainingTime = timeBehindMinutes(this, currentTime);

            if (remainingTime < DUE_TIME)
                return DUE_STRING;
            else if (remainingTime < 15)
                return Math.round(remainingTime) + " min";
        }

        if(use24hrTime)
            return this.to24hrTimeString();
        else
            return this.to12hrTimeString();
    }

    public String toFormattedDateString(boolean use24hrTime) {
        return  calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CANADA) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " +  toFormattedString(null, use24hrTime);
    }

    public String toDateString() {
        String dateString = "";
        calendar.setTimeInMillis(milliseconds);
        int month = (calendar.get(Calendar.MONTH) + 1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateString += calendar.get(Calendar.YEAR);
        dateString += "-" + (month >= 10 ? month : "0" + month);
        dateString += "-" + (day >= 10 ? day : "0" + day);

        return dateString;
    }

    public String toURLTimeString() {
        return toDateString() + "T" + to24hrTimeString() + ":00";
    }

    public String to24hrTimeString() {
        int hours = getHours();
        return ((hours < 10) ? "0" + getHoursString() : getHoursString()) + ":" + getMinutesString();
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return (int)(milliseconds - ((StopTime)another).getMilliseconds());
    }

    public void decreaseMilliSeconds(long time) {
        milliseconds -= time;
    }

    private int getMinutes() {
        calendar.setTimeInMillis(milliseconds);
        return  calendar.get(Calendar.MINUTE);
    }

    private int getHours() {
        calendar.setTimeInMillis(milliseconds);
        return  calendar.get(Calendar.HOUR_OF_DAY);
    }

    public void increaseMinute(int minutes) {
        milliseconds += minutes * 1000 * 60;
    }
}
