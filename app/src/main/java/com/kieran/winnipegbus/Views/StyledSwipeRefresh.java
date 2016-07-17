package com.kieran.winnipegbus.Views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.kieran.winnipegbus.R;

public class StyledSwipeRefresh extends SwipeRefreshLayout {
    public StyledSwipeRefresh(Context context) {
        super(context);
        initialize();
    }

    public StyledSwipeRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize() {
        setColorSchemeResources(R.color.rt_blue, R.color.rt_red);
    }
}
