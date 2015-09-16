package com.kieran.winnipegbus;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.kieran.winnipegbusbackend.FavouriteStop;

import java.util.List;

public class StopListAdapter extends ArrayAdapter<FavouriteStop> {
    Context context;
    int layoutResourceId;
    List<FavouriteStop> favouriteStops;

    public StopListAdapter(Context context, int layoutResourceId, List<FavouriteStop> favouriteStops) {
        super(context, layoutResourceId, favouriteStops);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.favouriteStops = favouriteStops;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StopHolder holder;

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopHolder();
            holder.stopNumber = (TextView)row.findViewById(R.id.favourites_stop_number);
            //holder.routeNumbers = (GridView)row.findViewById(R.id.gridview_stops_row_route_numbers);
           // holder.routeNumbers.setAdapter(new GridviewAdapter(context));
            holder.stopName = (TextView)row.findViewById(R.id.favourites_stop_name);

            row.setTag(holder);
        } else {
            holder = (StopHolder)row.getTag();
        }

        FavouriteStop favouriteStop = favouriteStops.get(position);
        holder.stopNumber.setText(Integer.toString(favouriteStop.getStopNumber()));
        holder.stopName.setText(favouriteStop.getStopName());

        return row;
    }

    static class StopHolder {
        TextView stopNumber;
        TextView stopName;
        GridView routeNumbers;
    }
}
