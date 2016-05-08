package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.UpcomingStop;

import java.util.List;

public class UpcomingStopsAdapter extends ArrayAdapter<UpcomingStop> {
    Context context;
    int layoutResourceId;
    List<UpcomingStop> upComingStops;
    private boolean use24hrTime;
    LayoutInflater inflater;

    public UpcomingStopsAdapter(Context context, int layoutResourceId, List<UpcomingStop> upcomingStops, boolean use24hrTime) {
        super(context, layoutResourceId, upcomingStops);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.upComingStops = upcomingStops;
        inflater = ((Activity)context).getLayoutInflater();
        this.use24hrTime = use24hrTime;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        StopHolder holder;

        if(row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopHolder();
            holder.stopNumber = (TextView)row.findViewById(R.id.upcoming_stop_number);
            holder.stopName = (TextView)row.findViewById(R.id.upcoming_stop_name);
            holder.time = (TextView)row.findViewById(R.id.upcoming_stop_time);
            row.setTag(holder);
        } else {
            holder = (StopHolder)row.getTag();
        }

        UpcomingStop upcomingStop = upComingStops.get(position);
        holder.upcomingStop = upcomingStop;
        holder.stopNumber.setText(Integer.toString(upcomingStop.getNumber()));
        holder.stopName.setText(upcomingStop.getName());
        holder.time.setText(upcomingStop.getTime().toFormattedString(null, use24hrTime));
        return row;
    }

    public static class StopHolder {
        TextView time;
        TextView stopName;
        TextView stopNumber;
        UpcomingStop upcomingStop;

        public UpcomingStop getUpcomingStop() {
            return upcomingStop;
        }
    }
}
