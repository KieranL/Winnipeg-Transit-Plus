package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.interfaces.ScheduledStopKey

class UpcomingStop(stop: Stop, time: StopTime, key: ScheduledStopKey) : Stop(stop), Comparable<Any> {
    var time: StopTime = time
        private set
    var key: ScheduledStopKey = key
        private set

    override operator fun compareTo(other: Any): Int {
        if(other !is UpcomingStop)
            return Int.MIN_VALUE
        return key.compareTo((other.key))
    }

}
