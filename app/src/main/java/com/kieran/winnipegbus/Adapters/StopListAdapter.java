package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.FavouriteStop;
import com.kieran.winnipegbusbackend.FavouriteStopsList;
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType;

import java.util.List;

public class StopListAdapter extends ArrayAdapter<FavouriteStop> {
    //public class MyAdapter<T> extends ArrayAdapter<T extends BasicDataModel>
    private int layoutResourceId;
    public static FavouritesListSortType sortPreference;
    private List<FavouriteStop> stops;
    private LayoutInflater inflater;

    public StopListAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId, FavouriteStopsList.getFavouriteStopsSorted(sortPreference));
        this.layoutResourceId = layoutResourceId;
        inflater = ((Activity)context).getLayoutInflater();
    }

    public StopListAdapter(Context context, int layoutResourceId, List<FavouriteStop> stops) {
        super(context, layoutResourceId, stops);
        this.stops = stops;
        this.layoutResourceId = layoutResourceId;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StopHolder holder;

        if(row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopHolder();
            holder.stopNumber = (TextView)row.findViewById(R.id.favourites_stop_number);
            holder.stopName = (TextView)row.findViewById(R.id.favourites_stop_name);

            row.setTag(holder);
        } else {
            holder = (StopHolder)row.getTag();
        }

        FavouriteStop favouriteStop;

        if(stops != null)
            favouriteStop = stops.get(position);
        else
            favouriteStop = FavouriteStopsList.get(position);

        holder.stopNumber.setText(Integer.toString(favouriteStop.getNumber()));
        holder.stopName.setText(favouriteStop.getDisplayName());

        return row;
    }

    static class StopHolder {
        TextView stopNumber;
        TextView stopName;
    }
}
