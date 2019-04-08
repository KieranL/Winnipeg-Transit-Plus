package com.kieran.winnipegbusbackend.common

import com.kieran.winnipegbusbackend.Stop
import com.kieran.winnipegbusbackend.interfaces.ScheduledStopKey

class UpcomingStop(stop: Stop, time: StopTime, key: ScheduledStopKey) : Stop(stop), Comparable<Any> {
    var time: StopTime = time
        private set
    var key: ScheduledStopKey = key
        private set

    override operator fun compareTo(other: Any): Int {
        return key.compareTo(((other as UpcomingStop).key))
    }

}
