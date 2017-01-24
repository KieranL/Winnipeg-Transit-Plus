package com.kieran.winnipegbustests;

import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Trip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class TripPlannerTest {
    @Test
    public void CompleteTripParse() throws JSONException {
        final String url = "https://api.winnipegtransit.com/v2/trip-planner.json?origin=addresses/130038&destination=addresses/203323&api-key=FTy2QN8ts293ZlhYP1t&usage=short";
        LoadResult<JSONObject> result = TransitApiManager.getJson(url);
        JSONObject object = result.getResult();
        JSONArray plans = object.getJSONArray("plans");
        Trip[] trips = new Trip[plans.length()];

        for(int i = 0; i < plans.length(); i++) {
            trips[i] = new Trip(plans.getJSONObject(i));
        }

        Assert.assertTrue(trips.length > 0);
    }
}
