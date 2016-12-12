package com.kieran.winnipegbus.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kieran.winnipegbus.R;
import com.kieran.winnipegbusbackend.Route;
import com.kieran.winnipegbusbackend.ScheduledStop;
import com.kieran.winnipegbusbackend.enums.CoverageTypes;

public class RouteNumberTextView extends TextView {
    public RouteNumberTextView(Context context) {
        super(context);
    }

    public RouteNumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RouteNumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("deprecation")
    public void setColour(ScheduledStop scheduledStop) {
        CoverageTypes coverageType = scheduledStop.getCoverageType();

        if (Route.isDownTownSpirit(scheduledStop.getRouteNumber())) {
            setTextColor(getResources().getColor(R.color.white));
            setBackgroundResource(R.drawable.route_number_background_dt_spirit);
        } else if (coverageType == CoverageTypes.REGULAR) {
            setTextColor(getResources().getColor(R.color.black));
            setBackgroundResource(R.drawable.route_number_background_regular);
        } else if (coverageType == CoverageTypes.EXPRESS || coverageType == CoverageTypes.SUPER_EXPRESS) {
            setTextColor(getResources().getColor(R.color.black));
            setBackgroundResource(R.drawable.route_number_background_express);
        } else if (coverageType == CoverageTypes.RAPID_TRANSIT) {
            setTextColor(getResources().getColor(R.color.white));
            setBackgroundResource(R.drawable.route_number_background_rt);
        }
    }
}
