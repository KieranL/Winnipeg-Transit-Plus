package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Segment;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Times;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip;

import java.util.List;

public class TripPlannerAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Trip> trips;
    private LayoutInflater inflater;
    private boolean use24hrTime;

    public TripPlannerAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public int getGroupCount() {
        return trips.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return trips.get(groupPosition).getSegments().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return trips.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return trips.get(groupPosition).getSegments().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View row = convertView;
        TripHolder holder;

        if(row == null) {
            row = inflater.inflate(R.layout.trip_planner_trip_row, parent, false);

            holder = new TripHolder();
            holder.timeRange = (TextView)row.findViewById(R.id.trip_time_range);

            row.setTag(holder);
        } else {
            holder = (TripHolder)row.getTag();
        }

        Trip trip = trips.get(groupPosition);
        Times times = trip.getTimes();
        holder.timeRange.setText(times.startTime.toFormattedString(null, use24hrTime) + " - " + times.endTime.toFormattedString(null, use24hrTime));

        return row;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View row = convertView;
        SegmentHolder holder;

        if(row == null) {
            row = inflater.inflate(R.layout.trip_planner_trip_segment_row, parent, false);

            holder = new SegmentHolder();
            holder.string = (TextView)row.findViewById(R.id.segment_string);
            holder.time = (TextView)row.findViewById(R.id.segment_time);

            row.setTag(holder);
        } else {
            holder = (SegmentHolder)row.getTag();
        }

        Trip trip = trips.get(groupPosition);
        Segment segment = trip.getSegments().get(childPosition);
        holder.string.setText(trips.get(groupPosition).getSegments().get(childPosition).toString());
        holder.time.setText(Integer.toString(segment.getTimes().totalTime));

        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class TripHolder {
        TextView timeRange;
    }

    private static class SegmentHolder {
        TextView string;
        TextView time;
    }
}