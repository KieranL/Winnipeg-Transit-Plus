package com.kieran.winnipegbustests;

import com.kieran.winnipegbusbackend.LoadResult;
import com.kieran.winnipegbusbackend.RouteKey;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;
import com.kieran.winnipegbusbackend.TransitApiManager;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

public class AppTests {
    @Test
    public void DateParseMilliseconds() {
        StopTime time = StopTime.convertStringToStopTime("2016-07-10T12:07:30");
        Assert.assertEquals(time.getMilliseconds(), 1468170450000L);
    }

    @Test
    public void RouteNumberSearchQueryGenerationShouldBeValid() {
        String url = TransitApiManager.generateSearchQuery(36).getQueryUrl();

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }

    @Test
    public void RouteKeySearchQueryGenerationShouldBeValid() {
        RouteKey key = new RouteKey(36, 1, 'M');
        String url = TransitApiManager.generateSearchQuery(key).getQueryUrl();

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }

    @Test
    public void ServiceAdvisoriesUrlGenerationShouldBeValid() {
        String url = TransitApiManager.generateServiceAdvisoriesUrl();

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }


    @Test
    public void GenericSearchQueryGenerationShouldBeValid() {
        String url = TransitApiManager.generateSearchQuery("Grant").getQueryUrl();

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }

    @Test
    public void StopFeaturesUrlGenerationShouldBeValid() {
        String url = TransitApiManager.generateStopFeaturesUrl(10545);

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }


    @Test
    public void StopNumberUrlGenerationShouldBeValid() {
        String url = TransitApiManager.generateStopNumberURL(10545, 11, new StopTime(), null);

        Assert.assertNotNull(TransitApiManager.getJson(url));
    }

    @Test
    public void StopScheduleCreationShouldReturnScheduledStops() {
        String url = TransitApiManager.generateStopNumberURL(10545, 11, new StopTime(), null);
        LoadResult<JSONObject> result = TransitApiManager.getJson(url);
        JSONObject dom = result.getResult();

        StopSchedule stopSchedule = new StopSchedule(dom);

        Assert.assertTrue(stopSchedule.getScheduledStopsSorted().size() > 0);
    }

    @Test
    public void StopTimeShouldCreateCorrect12HourString() {
        StopTime time = new StopTime(1468606042000L);

        Assert.assertEquals(time.to12hrTimeString(), "1:07p");
    }

    @Test
    public void StopTimeShouldCreateCorrect24HourString() {
        StopTime time = new StopTime(1468606042000L);

        Assert.assertEquals(time.to24hrTimeString(), "13:07");
    }
}
