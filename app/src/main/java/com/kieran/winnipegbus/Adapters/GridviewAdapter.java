package com.kieran.winnipegbus.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class GridviewAdapter extends BaseAdapter {
    private Context context;

    public GridviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if(convertView != null) {
            textView = (TextView) convertView;
        }else {
            textView = new TextView(context);
        }

        textView.setBackgroundColor(Color.BLUE);
        textView.setText("6");
        return textView;
    }
}
