package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.StopFeature;

import java.util.List;

public class StopFeaturesAdapter extends ArrayAdapter<StopFeature> {
    private int layoutResourceId;
    private List<StopFeature> stopFeatures;
    private LayoutInflater inflater;

    public StopFeaturesAdapter(Context context, int layoutResourceId, List<StopFeature> stopFeatures) {
        super(context, layoutResourceId, stopFeatures);
        this.layoutResourceId = layoutResourceId;
        this.stopFeatures = stopFeatures;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        StopHolder holder;

        if(row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new StopHolder();
            holder.name = (TextView)row.findViewById(R.id.stop_feature_name);
            holder.count = (TextView)row.findViewById(R.id.stop_feature_count);
            row.setTag(holder);
        } else {
            holder = (StopHolder)row.getTag();
        }

        StopFeature stopFeature = stopFeatures.get(position);
        holder.name.setText(stopFeature.getName());
        holder.count.setText(Integer.toString(stopFeature.getCount()));
        return row;
    }

    private static class StopHolder {
        TextView name;
        TextView count;
    }
}
