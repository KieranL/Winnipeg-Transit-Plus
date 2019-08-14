package com.kieran.winnipegbusbackend.common

import com.rollbar.android.Rollbar
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StopTime : Comparable<Any>, Serializable {
    var milliseconds: Long = 0
        private set

    private val minutesString: String
        get() {
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
        return "$hoursString:$minutesString"
    }

    fun to12hrTimeString(): String {
        var timeString: String
        val hours = hours

        timeString = when {
            hours == 0 -> "12:$minutesString"
            hours <= 12 -> "$hoursString:$minutesString"
            else -> (hours - 12).toString() + ":" + minutesString
        }

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
        dateString += "-" + if (month >= 10) month else "0$month"
        dateString += "-" + if (day >= 10) day else "0$day"

        return dateString
    }

    fun toURLTimeString(): String {
        return toDateString() + "T" + to24hrTimeString() + ":00"
    }

    fun to24hrTimeString(): String {
        val hours = hours
        return (if (hours < 10) "0$hoursString" else hoursString) + ":" + minutesString
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
        private const val DUE_TIME = 0.5
        private const val DUE_STRING = "Due"
        private const val EARLY_TIME = -0.5
        private const val LATE_TIME = 0.5
        private const val EARLY_TEXT = "Early"
        private const val LATE_TEXT = "Late"
        private const val OK_TEXT = "Ok"
        @Transient
        private val calendar = Calendar.getInstance()
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

        fun convertStringToStopTime(s: String, format: String): StopTime? {
            val dateFormat = SimpleDateFormat(format)

            return try {
                StopTime(dateFormat.parse(s))
            } catch (ex: ParseException) {
                Rollbar.instance()?.error(ex)
                null
            }
        }
    }
}
