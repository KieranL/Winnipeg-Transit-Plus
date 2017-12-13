package com.kieran.winnipegbusbackend

import android.annotation.SuppressLint

import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StopTime : Comparable<Any>, Serializable {
    var milliseconds: Long = 0
        private set

    val minutesString: String
        get() {
            val minutes = minutes
            return if (minutes >= 10) minutes.toString() else "0" + minutes.toString()
        }

    val hoursString: String
        get() = Integer.toString(hours)

    val minutes: Int
        get() {
            calendar.timeInMillis = milliseconds
            return calendar.get(Calendar.MINUTE)
        }

    val hours: Int
        get() {
            calendar.timeInMillis = milliseconds
            return calendar.get(Calendar.HOUR_OF_DAY)
        }

    val year: Int
        get() {
            calendar.timeInMillis = milliseconds
            return calendar.get(Calendar.YEAR)
        }

    val month: Int
        get() {
            calendar.timeInMillis = milliseconds
            return calendar.get(Calendar.MONTH)
        }

    val dayOfMonth: Int
        get() {
            calendar.timeInMillis = milliseconds
            return calendar.get(Calendar.DAY_OF_MONTH)
        }

    constructor() {
        milliseconds = System.currentTimeMillis()
    }

    constructor(date: Date) {
        milliseconds = date.time
    }

    constructor(milliseconds: Long) {
        this.milliseconds = milliseconds
    }

    fun increaseHour(increase: Int) {
        milliseconds += (increase * 1000 * 60 * 60).toLong()
    }

    override fun toString(): String {
        return hoursString + ":" + minutesString
    }

    fun to12hrTimeString(): String {
        var timeString: String
        val hours = hours

        if (hours == 0)
            timeString = "12" + ":" + minutesString
        else if (hours <= 12)
            timeString = hoursString + ":" + minutesString
        else
            timeString = (hours - 12).toString() + ":" + minutesString

        timeString += if (hours >= 12) "p" else "a"

        return timeString
    }

    fun toFormattedString(currentTime: StopTime?, use24hrTime: Boolean): String {
        if (currentTime != null) {
            val remainingTime = timeBehindMinutes(this, currentTime)

            if (remainingTime < DUE_TIME)
                return DUE_STRING
            else if (remainingTime < 15)
                return Math.round(remainingTime).toString() + " min"
        }

        return if (use24hrTime)
            this.to24hrTimeString()
        else
            this.to12hrTimeString()
    }

    fun toFormattedDateString(use24hrTime: Boolean): String {
        calendar.timeInMillis = milliseconds
        return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.CANADA) + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + toFormattedString(null, use24hrTime)
    }

    fun toDateString(): String {
        var dateString = ""
        calendar.timeInMillis = milliseconds
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        dateString += calendar.get(Calendar.YEAR)
        dateString += "-" + if (month >= 10) month else "0" + month
        dateString += "-" + if (day >= 10) day else "0" + day

        return dateString
    }

    fun toURLTimeString(): String {
        return toDateString() + "T" + to24hrTimeString() + ":00"
    }

    fun to24hrTimeString(): String {
        val hours = hours
        return (if (hours < 10) "0" + hoursString else hoursString) + ":" + minutesString
    }

    override operator fun compareTo(other: Any): Int {
        return (milliseconds - (other as StopTime).milliseconds).toInt()
    }

    fun decreaseMilliSeconds(time: Long) {
        milliseconds -= time
    }

    fun increaseMinute(minutes: Int) {
        milliseconds += (minutes * 1000 * 60).toLong()
    }

    fun toDatePickerDateFormat(): String {
        return datePickerDateFormat.format(Date(milliseconds))
    }

    companion object {
        private val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        private val DUE_TIME = 0.5
        private val DUE_STRING = "Due"
        private val EARLY_TIME = -0.5
        private val LATE_TIME = 0.5
        private val EARLY_TEXT = "Early"
        private val LATE_TEXT = "Late"
        private val OK_TEXT = "Ok"
        @Transient private val calendar = Calendar.getInstance()
        private val datePickerDateFormat = SimpleDateFormat("EEE, MMM dd", Locale.CANADA)

        fun timeBehindMinutes(estimated: StopTime, scheduled: StopTime): Double {
            return ((estimated.milliseconds - scheduled.milliseconds) / 1000 / 60).toDouble()
        }

        fun getTimeStatus(estimated: StopTime, scheduled: StopTime): String {
            val timeStatusNum = timeBehindMinutes(estimated, scheduled)

            return if (timeStatusNum < LATE_TIME && timeStatusNum > EARLY_TIME)
                OK_TEXT
            else if (timeStatusNum <= EARLY_TIME)
                EARLY_TEXT
            else
                LATE_TEXT
        }

        fun convertStringToStopTime(s: String): StopTime? {
            @SuppressLint("SimpleDateFormat")
            val dateFormat = SimpleDateFormat(DATE_FORMAT)

            return try {
                StopTime(dateFormat.parse(s))
            } catch (e: ParseException) {
                null
            }

        }
    }
}
