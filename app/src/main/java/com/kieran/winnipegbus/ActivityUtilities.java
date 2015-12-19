package com.kieran.winnipegbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.kieran.winnipegbus.Activities.HomeScreenActivity;
import com.kieran.winnipegbus.Activities.StopTimesActivity;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

public class ActivityUtilities {
    public static void createLongToaster(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void openStopTimes(Context context, int stopNumber) {
        Intent intent = new Intent(context, StopTimesActivity.class);

        intent.putExtra(HomeScreenActivity.STOP_NUMBER, stopNumber);
        context.startActivity(intent);
    }

    public static boolean getTimeSetting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("pref_use_24hr_time", false);
    }

    public static void setTextViewColour(Context context, TextView textView, ScheduledStop scheduledStop) {
        int coverageTypeId = scheduledStop.getCoverageTypeId();

        if(scheduledStop.getRouteNumber() < 10) {
            textView.setTextColor(context.getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.route_number_background_dt_spirit);
        }else if(coverageTypeId == CoverageTypes.REGULAR.typeId) {
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.route_number_background_regular);
        } else if(coverageTypeId == CoverageTypes.EXPRESS.typeId || coverageTypeId == CoverageTypes.SUPER_EXPRESS.typeId) {
            textView.setTextColor(context.getResources().getColor(R.color.black));
            textView.setBackgroundResource(R.drawable.route_number_background_express);
        } else if (coverageTypeId == CoverageTypes.RAPID_TRANSIT.typeId) {
            textView.setTextColor(context.getResources().getColor(R.color.white));
            textView.setBackgroundResource(R.drawable.route_number_background_rt);
        }
    }
}
