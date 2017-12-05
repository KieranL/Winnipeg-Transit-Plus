package com.kieran.winnipegbus

import android.app.Activity

import java.util.ArrayList

object ActivityManager {
    private val activities = ArrayList<Activity>()

    fun refreshThemes() {
        for (activity in activities)
            activity.recreate()
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }
}
