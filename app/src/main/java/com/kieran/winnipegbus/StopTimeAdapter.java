package com.kieran.winnipegbus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbusbackend.ScheduledStopInfo;

import java.util.List;

public class StopTimeAdapter extends ArrayAdapter<ScheduledStopInfo>{
    Context context;
    int layoutResourceId;
    List<ScheduledStopInfo> scheduledStopInfos;

    public StopTimeAdapter(Context context, int layoutResourceId, List<ScheduledStopInfo> scheduledStopInfos) {
        super(context, layoutResourceId, scheduledStopInfos);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.scheduledStopInfos = scheduledStopInfos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StopTimeHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
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

        ScheduledStopInfo scheduledStopInfo = scheduledStopInfos.get(position);
        holder.routeNumber.setText(Integer.toString(scheduledStopInfo.getRouteNumber()));
        holder.routeVariantName.setText(scheduledStopInfo.getRouteVariantName());
        holder.timeStatus.setText(scheduledStopInfo.getTimeStatus());
        holder.departureTime.setText(scheduledStopInfo.getEstimatedDepartureTime().toString());

        return row;
    }

    static class StopTimeHolder {
        TextView routeNumber;
        TextView routeVariantName;
        TextView timeStatus;
        TextView departureTime;
    }
}
