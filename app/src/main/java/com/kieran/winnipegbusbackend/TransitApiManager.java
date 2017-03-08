package com.kieran.winnipegbusbackend;

import android.location.Location;
import android.os.AsyncTask;

import com.kieran.winnipegbusbackend.enums.SearchQueryType;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TransitApiManager {
    private static final int START_TIME_DECREASE = 10000;
    private static final String QUERY_TIME = "query-time";
    private final static String ROUTE_PARAMETER = "route";
    private final static String END_TIME_PARAMETER = "end";
    private static final String START_TIME_PARAMETER = "start";
    private static final String STOP_FEATURE_PARAMETER = "features";
    private static final String VARIANT_PARAMETER = "variant";
    private static final String STOPS_PARAMETER = "stops";
    private static final String FORWARD_SLASH = "/";
    private static final String COLON = ":";
    private static final String SCHEDULE_PARAMETER = "schedule";
    private static final String DISTANCE_PARAMETER = "distance";
    private static final String LATITUDE_PARAMETER = "lat";
    private static final String LONGITUDE_PARAMETER = "lon";
    private static final String SERVICE_ADVISORIES_PARAMETER = "service-advisories";
    private static final String LOCATIONS_PARAMETER = "locations";
    private static final String URL_FORMAT = "http://api.winnipegtransit.com/v2/%s.json?usage=short&api-key=FTy2QN8ts293ZlhYP1t%s";
    public static StopTime lastQueryTime = new StopTime();

    public static LoadResult<JSONObject> getJson(String path) {
        try {
            URL url = new URL(path);
            java.util.Scanner s = new java.util.Scanner(url.openStream()).useDelimiter("\\A");
            String myString = s.hasNext() ? s.next() : "";

            JSONObject obj = new JSONObject(myString);
            lastQueryTime = StopTime.convertStringToStopTime(obj.getString(QUERY_TIME));

            return new LoadResult<>(obj, null);
        } catch (Exception ex) {
            return new LoadResult<>(null, ex);
        }
    }

    public static AsyncTask getJsonAsync(final String path, final OnJsonLoadResultReceiveListener listener) {
        return new AsyncTask<String, Void, LoadResult<JSONObject>>() {
            @Override
            protected LoadResult<JSONObject> doInBackground(String... strings) {
                return getJson(path);
            }

            @Override
            protected void onPostExecute(LoadResult<JSONObject> result) {
                listener.OnReceive(result);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
    }

    public static String generateStopNumberURL(int stopNumber, List<Integer> routeNumbers, StopTime startTime, StopTime endTime) {
        URLParameter[] parameters = new URLParameter[3];

        if (routeNumbers != null)
            parameters[0] = new URLParameter(ROUTE_PARAMETER, routeNumbers);

        if (startTime != null) {
            startTime.decreaseMilliSeconds(START_TIME_DECREASE); //decrease start time for API inconsistency? not sure what the reason this is for

            parameters[1] = new URLParameter(START_TIME_PARAMETER, startTime.toURLTimeString());
            startTime.decreaseMilliSeconds(-START_TIME_DECREASE);
        }

        if (endTime != null)
            parameters[2] = new URLParameter(END_TIME_PARAMETER, endTime.toURLTimeString());

        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + stopNumber + FORWARD_SLASH + SCHEDULE_PARAMETER, parameters);
    }

    public static String generateStopNumberURL(int stopNumber, int routeNumber, StopTime startTime, StopTime endTime) {
        List<Integer> routeFilter = new ArrayList<>();
        routeFilter.add(routeNumber);

        return generateStopNumberURL(stopNumber, routeFilter, startTime, endTime);
    }

    public static SearchQuery generateSearchQuery(String search) {
        try {
            int routeNumber = Integer.parseInt(search);
            return generateSearchQuery(routeNumber);
        } catch (Exception e) {
            return new SearchQuery(search, createUrl(STOPS_PARAMETER + COLON + createURLFriendlyString(search), null), SearchQueryType.GENERAL);
        }
    }

    public static SearchQuery generateSearchQuery(int routeNumber) {
        URLParameter[] parameters = new URLParameter[]{new URLParameter(ROUTE_PARAMETER, Integer.toString(routeNumber))};
        String url = createUrl(STOPS_PARAMETER, parameters);

        return new SearchQuery(Integer.toString(routeNumber), url, SearchQueryType.ROUTE_NUMBER);
    }

    public static SearchQuery generateSearchQuery(RouteKey key) {
        URLParameter[] parameters = new URLParameter[]{new URLParameter(VARIANT_PARAMETER, key.getKeyString())};
        String url = createUrl(STOPS_PARAMETER, parameters);

        return new SearchQuery(key.getKeyString(), url, SearchQueryType.ROUTE_NUMBER);
    }

    public static String generateStopFeaturesUrl(int stopNumber) {
        return createUrl(STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber) + FORWARD_SLASH + STOP_FEATURE_PARAMETER, null);
    }

    public static String generateServiceAdvisoriesUrl() {
        return createUrl(SERVICE_ADVISORIES_PARAMETER, null);
    }

    private static String createURLFriendlyString(String s) {
        return s.replaceAll("\\s+", "+");
    }

    public static SearchQuery generateSearchQuery(Location location, int radius) {
        int totalRadius = Math.round(location.getAccuracy()) + radius;
        URLParameter[] parameters = new URLParameter[]{new URLParameter(DISTANCE_PARAMETER, Integer.toString(totalRadius)), new URLParameter(LATITUDE_PARAMETER, Double.toString(location.getLatitude())), new URLParameter(LONGITUDE_PARAMETER, Double.toString(location.getLongitude()))};
        String url = createUrl(STOPS_PARAMETER, parameters);
        return new SearchQuery("NearbyStops", url, SearchQueryType.NEARBY);
    }

    public static String generateLocationQueryUrl(String query) {
        return createUrl(LOCATIONS_PARAMETER + COLON + createURLFriendlyString(query), null);
    }

    private static String createUrl(String path, URLParameter[] parameters) {
        String parameterString = "";

        if (parameters != null) {
            for (URLParameter p : parameters) {
                if(p != null)
                    parameterString += "&" + p.toString();
            }
        }

        return String.format(URL_FORMAT, path, parameterString);
    }

    public interface OnJsonLoadResultReceiveListener {
        void OnReceive(LoadResult<JSONObject> result);
    }
}