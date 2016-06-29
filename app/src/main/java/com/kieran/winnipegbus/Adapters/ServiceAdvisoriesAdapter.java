package com.kieran.winnipegbus.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kieran.winnipegbus.Activities.BaseActivity;
import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.ServiceAdvisories.ServiceAdvisory;

import java.util.List;

public class ServiceAdvisoriesAdapter extends ArrayAdapter<ServiceAdvisory> {
    private int layoutResourceId;
    private List<ServiceAdvisory> advisories;
    private LayoutInflater inflater;
    private boolean use24hrTime;
    private Context context;

    public ServiceAdvisoriesAdapter(Context context, int layoutResourceId, List<ServiceAdvisory> advisories) {
        super(context, layoutResourceId, advisories);
        this.context = context;
        this.advisories = advisories;
        this.layoutResourceId = layoutResourceId;
        inflater = ((Activity)context).getLayoutInflater();
        loadTimeSetting();
    }

    public void loadTimeSetting() {
        use24hrTime = BaseActivity.getTimeSetting(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AdvisoryHolder holder;

        if(row == null) {
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new AdvisoryHolder();
            holder.title = (TextView)row.findViewById(R.id.service_advisory_title);
            holder.updatedTime = (TextView)row.findViewById(R.id.service_advisory_updated);

            row.setTag(holder);
        } else {
            holder = (AdvisoryHolder)row.getTag();
        }

        ServiceAdvisory serviceAdvisory = advisories.get(position);

        holder.title.setText(serviceAdvisory.getTitle());
        holder.updatedTime.setText(serviceAdvisory.getUpdatedAt().toFormattedDateString(use24hrTime));

        return row;
    }

    private static class AdvisoryHolder {
        TextView title;
        TextView updatedTime;
    }
}
