package com.kieran.winnipegbus;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static List<Activity> activities = new ArrayList<>();

    public static void refreshThemes() {
        for (Activity activity : activities)
            activity.recreate();
    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }
}
