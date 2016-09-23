package com.kieran.winnipegbusbackend.UpcomingStops;

import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.RouteKey;
import com.kieran.winnipegbusbackend.SearchQuery;
import com.kieran.winnipegbusbackend.TransitApiManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HttpUpcomingStopsManager implements UpcomingStopsManager {
    @Override
    public void GetUpcomingStopsAsync(RouteKey key, int stopOnRoute, final OnUpcomingStopsFoundListener listener) {
        SearchQuery query = TransitApiManager.generateSearchQuery(key);
        TransitApiManager.getJsonAsync(query.getQueryUrl(), new TransitApiManager.OnJsonLoadResultReceiveListener() {
            @Override
            public void OnReceive(LoadResult<JSONObject> result) {
                LoadResult<ArrayList<Integer>> loadResult = new LoadResult<>(null, null);

                if (result.getResult() != null) {
                    try {
                        JSONArray stops  = (result.getResult()).getJSONArray("stops");
                        ArrayList<Integer> stopNumbers = new ArrayList<>();

                        for (int i = 0; i < stops.length(); i++) {
                            stopNumbers.add(stops.getJSONObject(i).getInt("number"));
                        }

                        loadResult.setResult(stopNumbers);
                    } catch (JSONException ex) {
                        loadResult.setException(ex);
                    }
                }

                listener.OnUpcomingStopsFound(loadResult);
            }
        });
    }
}
