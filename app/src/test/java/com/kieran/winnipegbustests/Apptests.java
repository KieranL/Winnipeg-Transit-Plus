package com.kieran.winnipegbustests;

import com.kieran.winnipegbusbackend.BusUtilities;
import com.kieran.winnipegbusbackend.RouteKey;
import com.kieran.winnipegbusbackend.StopSchedule;
import com.kieran.winnipegbusbackend.StopTime;

import junit.framework.Assert;

import org.junit.Test;
import org.w3c.dom.Document;

public class AppTests {
    @Test
    public void DateParseMilliseconds() {
        StopTime time = BusUtilities.convertToStopTime("2016-07-10T12:07:30");
        Assert.assertEquals(time.getMilliseconds(), 1468170450000L);
    }

    @Test
    public void RouteNumberSearchQueryGenerationShouldBeValid() {
        String url = BusUtilities.generateSearchQuery(36).getQueryUrl();

        Assert.assertNotNull(BusUtilities.getXML(url));
    }

    @Test
    public void RouteKeySearchQueryGenerationShouldBeValid() {
        RouteKey key = new RouteKey(36, 1, 'M');
        String url = BusUtilities.generateSearchQuery(key).getQueryUrl();

        Assert.assertNotNull(BusUtilities.getXML(url));
    }

    @Test
    public void ServiceAdvisoriesUrlGenerationShouldBeValid() {
        String url = BusUtilities.generateServiceAdvisoriesUrl();

        Assert.assertNotNull(BusUtilities.getXML(url));
    }


    @Test
    public void GenericSearchQueryGenerationShouldBeValid() {
        String url = BusUtilities.generateSearchQuery("Grant").getQueryUrl();

        Assert.assertNotNull(BusUtilities.getXML(url));
    }

    @Test
    public void StopFeaturesUrlGenerationShouldBeValid() {
        String url = BusUtilities.generateStopFeaturesUrl(10545);

        Assert.assertNotNull(BusUtilities.getXML(url));
    }


    @Test
    public void StopNumberUrlGenerationShouldBeValid() {
        String url = BusUtilities.generateStopNumberURL(10545, 11, new StopTime(), null);

        Assert.assertNotNull(BusUtilities.getXML(url));
    }

    @Test
    public void StopScheduleCreationShouldReturnScheduledStops() {
        String url = BusUtilities.generateStopNumberURL(10545, 11, new StopTime(), null);
        Document dom = (Document) BusUtilities.getXML(url).getResult();

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
