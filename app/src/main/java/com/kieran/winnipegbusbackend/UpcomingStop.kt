package com.kieran.winnipegbusbackend

class UpcomingStop : Stop, Comparable<*> {
    var time: StopTime? = null
        private set
    var key: ScheduledStopKey? = null
        private set

    constructor(stopName: String, stopNumber: Int, time: StopTime, key: ScheduledStopKey) : super(stopName, stopNumber) {
        this.time = time
        this.key = key
    }

    constructor(stop: Stop, time: StopTime, key: ScheduledStopKey) : super(stop) {
        this.time = time
        this.key = key
    }

    override operator fun compareTo(another: Any): Int {
        return key!!.stopNumber - (another as UpcomingStop).key!!.stopNumber
    }

}
