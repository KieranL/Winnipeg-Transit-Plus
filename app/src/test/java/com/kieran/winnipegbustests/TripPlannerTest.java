package com.kieran.winnipegbustests;

import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.TransitApiManager;
import com.kieran.winnipegbusbackend.TripPlanner.LocationFactory;
import com.kieran.winnipegbusbackend.TripPlanner.classes.Location;
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

    @Test
    public void GetAddressLocationFromQueryAndParse() throws JSONException {
        String url = TransitApiManager.generateLocationQueryUrl("1099 Grant");
        LoadResult<JSONObject> result = TransitApiManager.getJson(url);
        JSONArray locations = result.getResult().getJSONArray("locations");
        Location location = LocationFactory.createLocation(locations.getJSONObject(0));

        Assert.assertNotNull(location);
        Assert.assertTrue(location.getTitle().equals("1099 GrantAve"));
    }

    @Test
    public void GetIntersectionLocationFromQueryAndParse() throws JSONException {
        String url = TransitApiManager.generateLocationQueryUrl("Grant at Stafford");
        LoadResult<JSONObject> result = TransitApiManager.getJson(url);
        JSONArray locations = result.getResult().getJSONArray("locations");
        Location location = LocationFactory.createLocation(locations.getJSONObject(0));

        Assert.assertNotNull(location);
        Assert.assertTrue(location.getTitle().equals("GrantAve at StaffordSt"));
    }

    @Test
    public void GetMonumentLocationFromQueryAndParse() throws JSONException {
        String url = TransitApiManager.generateLocationQueryUrl("mts");
        LoadResult<JSONObject> result = TransitApiManager.getJson(url);
        JSONArray locations = result.getResult().getJSONArray("locations");
        Location location = LocationFactory.createLocation(locations.getJSONObject(0));

        Assert.assertNotNull(location);
        Assert.assertTrue(location.getTitle().equals("Bell MTS Place"));
    }
}
