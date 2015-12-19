package com.kieran.winnipegbus;


import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    private static List<Activity> activities = new ArrayList<>();
    public static boolean hasThemeChanged = false;

    public static void refreshThemes() {
        hasThemeChanged = true;
        for (Activity activity : activities) {
            activity.recreate();
        }

    }

    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
}
