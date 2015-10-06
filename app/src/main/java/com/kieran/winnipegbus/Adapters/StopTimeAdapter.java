package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.StopTime;

import java.util.Date;
import java.util.List;

public class StopTimeAdapter extends ArrayAdapter<ScheduledStop>{
    Context context;
    int layoutResourceId;
    boolean use24hrTime;
    LayoutInflater inflater;
    List<ScheduledStop> scheduledStops;

    public StopTimeAdapter(Context context, int layoutResourceId, List<ScheduledStop> scheduledStops) {
        super(context, layoutResourceId, scheduledStops);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.scheduledStops = scheduledStops;
        inflater = ((Activity)context).getLayoutInflater();

        getTimeSetting();
    }

    public void getTimeSetting() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        use24hrTime = prefs.getBoolean("pref_use_24hr_time", false);
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        StopTimeHolder holder;

        if(row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopTimeHolder();
            holder.routeNumber = (TextView)row.findViewById(R.id.route_number_text);
            holder.routeVariantName = (TextView)row.findViewById(R.id.route_name_text);
            holder.timeStatus = (TextView)row.findViewById(R.id.time_status_text);
            holder.departureTime = (TextView)row.findViewById(R.id.departure_time_text);

            row.setTag(holder);
        } else {
            holder = (StopTimeHolder)row.getTag();
        }

        ScheduledStop scheduledStop = scheduledStops.get(position);
        holder.routeNumber.setText(Integer.toString(scheduledStop.getRouteNumber()));
        holder.routeVariantName.setText(scheduledStop.getRouteVariantName());
        holder.timeStatus.setText(scheduledStop.getTimeStatus());
        holder.departureTime.setText(scheduledStop.getEstimatedDepartureTime().toFormattedString(new StopTime(new Date()), use24hrTime));

        return row;
    }

    private static class StopTimeHolder {
        TextView routeNumber;
        TextView routeVariantName;
        TextView timeStatus;
        TextView departureTime;
    }
}
