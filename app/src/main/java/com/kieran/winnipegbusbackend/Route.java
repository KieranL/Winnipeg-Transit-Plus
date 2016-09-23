package com.kieran.winnipegbusbackend;

import android.support.annotation.NonNull;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Route implements Comparable, Serializable {
    private final static String ROUTE_TAG = "route";
    private final static String ROUTE_COVERAGE_TAG = "coverage";
    private final static String ROUTE_NUMBER_TAG = "number";
    private final static String ROUTE_NAME_TAG = "name";
    public static final int DT_SPIRIT_MAX_RT_NUM = 10;

    protected int routeNumber;
    protected String routeName;
    protected CoverageTypes coverageType;

        public Route(Route route) {
        routeNumber = route.getRouteNumber();
        routeName =  route.getRouteName();
    }

    public Route(JSONObject jsonObject) {
        try {
            jsonObject = jsonObject.getJSONObject(ROUTE_TAG);

            coverageType = CoverageTypes.getEnum(jsonObject.getString(ROUTE_COVERAGE_TAG));
            routeName = jsonObject.getString(ROUTE_NAME_TAG);
            routeNumber = jsonObject.getInt(ROUTE_NUMBER_TAG);
        } catch (JSONException e) {
            //Intentionally blank because occasionally Winnipeg Transits API leaves out some fields
        }
    }

    public int getRouteNumber() {
        return routeNumber;
    }

    public String getRouteName() {
        return routeName;
    }

    public CoverageTypes getCoverageType() {
        return coverageType;
    }

    public String toString(){
        return routeNumber + " " + ((routeName != null) ? routeName : "");
    }

    @Override
    public int compareTo(@NonNull Object another) {
        return routeNumber - ((Route)another).getRouteNumber();
    }

    public boolean isDownTownSpirit() {
        return isDownTownSpirit(routeNumber);
    }

    public static boolean isDownTownSpirit(int routeNumber) {
        return routeNumber < DT_SPIRIT_MAX_RT_NUM;
    }
}
