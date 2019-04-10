package com.kieran.winnipegbusbackend

import com.kieran.winnipegbusbackend.winnipegtransit.WinnipegTransitScheduledStopKey

class UpcomingStop : Stop, Comparable<Any> {
    var time: StopTime? = null
        private set
    var key: WinnipegTransitScheduledStopKey? = null
        private set

    constructor(stopName: String, stopNumber: Int, time: StopTime, key: WinnipegTransitScheduledStopKey) : super(stopName, stopNumber) {
        this.time = time
        this.key = key
    }

    constructor(stop: Stop, time: StopTime, key: WinnipegTransitScheduledStopKey) : super(stop) {
        this.time = time
        this.key = key
    }

    override operator fun compareTo(another: Any): Int {
        return key!!.stopNumber - (another as UpcomingStop).key!!.stopNumber
    }

}
