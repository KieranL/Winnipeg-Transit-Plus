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
    private final static String API_KEY = "FTy2QN8ts293ZlhYP1t";
    private final static String API_URL = "http://api.winnipegtransit.com/v2/";
    private final static String USAGE = "usage=short&api-key=";
    private final static String ROUTE_PARAMETER = "route=";
    private final static String END_TIME_PARAMETER = "end=";
    private static final String START_TIME_PARAMETER = "start=";
    private static final String STOP_FEATURE_PARAMETER = "features";
    private static final String VARIANT_PARAMETER = "variant=";
    private static final String API_KEY_PARAMETER = "api-key=";
    private static final String STOPS_PARAMETER = "stops";
    private static final String AMPERSAND = "&";
    private static final String FORWARD_SLASH = "/";
    private static final String COLON = ":";
    private static final String QUESTION_MARK = "?";
    private static final String SCHEDULE_PARAMETER = "schedule";
    private static final String DISTANCE_PARAMETER = "distance=";
    private static final String LATITUDE_PARAMETER = "lat=";
    private static final String LONGITUDE_PARAMETER = "lon=";
    private static final String SERVICE_ADVISORIES_PARAMETER = "service-advisories";
    private static final String JSON_PARAMETER = ".json";
    private static final String LOCATIONS_PARAMETER = "locations";
    public static StopTime lastQueryTime = new StopTime();

    public static LoadResult<JSONObject> getJson(String path) {
        try{
            URL url = new URL(path);
            java.util.Scanner s = new java.util.Scanner(url.openStream()).useDelimiter("\\A");
            String myString = s.hasNext() ? s.next() : "";

            JSONObject obj = new JSONObject(myString);
            lastQueryTime = StopTime.convertStringToStopTime(obj.getString(QUERY_TIME));

            return new LoadResult<>(obj, null);
        }catch(Exception ex) {
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
        String routeFilter = "";
        String startTimeFilter = "";
        String endTimeFilter = "";

        if (routeNumbers != null) {
            String routes = "";
            for (int i = 0; i < routeNumbers.size(); i++) {
                routes += Integer.toString(routeNumbers.get(i));
                if (i < routeNumbers.size() - 1)
                    routes += ",";
            }
            routeFilter = (ROUTE_PARAMETER + routes + AMPERSAND);
        }

        if(startTime != null) {
            startTime.decreaseMilliSeconds(START_TIME_DECREASE); //decrease start time for API inconsistency? not sure what the reason this is for
            startTimeFilter += START_TIME_PARAMETER + startTime.toURLTimeString();
            startTimeFilter += AMPERSAND;
            startTime.decreaseMilliSeconds(-START_TIME_DECREASE);
        }

        if(endTime != null) {
            endTimeFilter += END_TIME_PARAMETER + endTime.toURLTimeString();
            endTimeFilter += AMPERSAND;
        }
        String url = API_URL + STOPS_PARAMETER + FORWARD_SLASH + stopNumber + FORWARD_SLASH + SCHEDULE_PARAMETER;

        url += JSON_PARAMETER;
        url += QUESTION_MARK + startTimeFilter + endTimeFilter + routeFilter + USAGE + API_KEY;

        return  url;
    }

    public static String generateStopNumberURL(int stopNumber, int routeNumber, StopTime startTime, StopTime endTime) {
        List<Integer> routeFilter = new ArrayList<>();
        routeFilter.add(routeNumber);

        return generateStopNumberURL(stopNumber, routeFilter, startTime, endTime);
    }

    public static SearchQuery generateSearchQuery(String search) {
        try{
            int routeNumber = Integer.parseInt(search);
            return generateSearchQuery(routeNumber);
        }catch (Exception e) {
            return new SearchQuery(search, API_URL + STOPS_PARAMETER + COLON + createURLFriendlyString(search) + JSON_PARAMETER + QUESTION_MARK + USAGE + API_KEY, SearchQueryType.GENERAL);
        }
    }

    public static SearchQuery generateSearchQuery(int routeNumber) {
        return new SearchQuery(Integer.toString(routeNumber), API_URL + STOPS_PARAMETER + JSON_PARAMETER + QUESTION_MARK + ROUTE_PARAMETER + routeNumber + AMPERSAND + USAGE + API_KEY, SearchQueryType.ROUTE_NUMBER);
    }

    public static SearchQuery generateSearchQuery(RouteKey key) {
        return new SearchQuery(key.getKeyString(), API_URL + STOPS_PARAMETER + JSON_PARAMETER + QUESTION_MARK + VARIANT_PARAMETER + key.getKeyString() + AMPERSAND + USAGE + API_KEY, SearchQueryType.ROUTE_NUMBER);
    }

    public static String generateStopFeaturesUrl(int stopNumber) {
        return API_URL + STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber) + FORWARD_SLASH + STOP_FEATURE_PARAMETER + JSON_PARAMETER + QUESTION_MARK + USAGE + API_KEY;
    }

    public static String generateServiceAdvisoriesUrl() {
        return API_URL + SERVICE_ADVISORIES_PARAMETER + JSON_PARAMETER + QUESTION_MARK + API_KEY_PARAMETER + API_KEY;
    }

    private static String createURLFriendlyString(String s) {
        String[] words = s.split("\\s+");
        String urlFriendlyString = words[0];

        for(int i = 1; i < words.length; i++)
            urlFriendlyString += "+" + words[i];

        return urlFriendlyString;
    }

    public static SearchQuery generateSearchQuery(Location location, int radius) {
        int totalRadius = Math.round(location.getAccuracy()) + radius;
        String url = API_URL + STOPS_PARAMETER + JSON_PARAMETER + QUESTION_MARK + DISTANCE_PARAMETER + totalRadius + AMPERSAND + LATITUDE_PARAMETER + location.getLatitude() + AMPERSAND + LONGITUDE_PARAMETER + location.getLongitude() + AMPERSAND + USAGE + API_KEY;
        return new SearchQuery("NearbyStops", url, SearchQueryType.NEARBY);
    }

    public static String generateLocationQueryUrl(String query) {
        return API_URL + LOCATIONS_PARAMETER + COLON + createURLFriendlyString(query) + JSON_PARAMETER + QUESTION_MARK + USAGE + AMPERSAND + API_KEY_PARAMETER + API_KEY;
    }

    public interface OnJsonLoadResultReceiveListener {
        void OnReceive(LoadResult<JSONObject> result);
    }
}