package com.kieran.winnipegbusbackend.UpcomingStops;

import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.RouteKey;

import java.util.ArrayList;

public interface UpcomingStopsManager {
    void GetUpcomingStopsAsync(RouteKey key, int stopOnRoute, OnUpcomingStopsFoundListener listener);

    interface OnUpcomingStopsFoundListener {
        void OnUpcomingStopsFound(LoadResult<ArrayList<Integer>> stops);
    }
}
