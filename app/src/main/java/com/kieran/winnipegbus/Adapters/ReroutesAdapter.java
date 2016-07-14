package com.kieran.winnipegbus.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kieran.winnipegbusbackend.ServiceAdvisories.Reroute;

import java.util.List;

public class ReroutesAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Reroute> reroutes;

    public ReroutesAdapter(Context context, List<Reroute> reroutes) {
        this.context = context;
        this.reroutes = reroutes;
    }

    @Override
    public int getGroupCount() {
        return reroutes.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return reroutes.get(i).getInstructions().size();
    }

    @Override
    public Object getGroup(int i) {
        return reroutes.get(i);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reroutes.get(groupPosition).getInstructions().get(childPosition);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        String s = ((Reroute)getGroup(groupPosition)).getHeading();
        textView.setText(s);
        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        TextView textView = new TextView(context);
        String s = ((Reroute)getGroup(groupPosition)).getInstructions().get(childPosition);
        textView.setText(s);
        return textView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
