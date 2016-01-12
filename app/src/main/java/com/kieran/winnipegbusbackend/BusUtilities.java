package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbusbackend.enums.CoverageTypes;
import com.kieran.winnipegbusbackend.enums.SearchQueryTypeIds;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BusUtilities {
    private final static String API_KEY = "FTy2QN8ts293ZlhYP1t";
    private final static String API_URL = "http://api.winnipegtransit.com/v2/";
    private final static String USAGE = "usage=short&api-key=";
    private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
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

    public static StopTime convertToDate(String s) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            return new StopTime(dateFormat.parse(s));
        } catch (ParseException e) {
            return null;
        }
    }

    public static String getValue(String tag, Node originalNode) {
        try {
            Node node = ((Element)originalNode).getElementsByTagName(tag).item(0).getFirstChild();
            return node.getNodeValue();
        }catch (Exception e) {
            return null;
        }
    }

    public static LoadResult getXML(String path) {
        try {
            return getXML(new URL(path).openStream());
        } catch (IOException e) {
            return new LoadResult(null, e);
        }
    }

    public static LoadResult getXML(InputStream inputStream)  {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document XMLDocument = db.parse(inputStream);

            return new LoadResult(XMLDocument, null);
        } catch (Exception e) {
            return new LoadResult(null, e);
        }
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
            startTimeFilter += START_TIME_PARAMETER + startTime.to24hrTimeString();
            startTimeFilter += AMPERSAND;
        }

        if(endTime != null) {
            endTimeFilter += END_TIME_PARAMETER + endTime.to24hrTimeString();
            endTimeFilter += AMPERSAND;
        }

        return API_URL + STOPS_PARAMETER + FORWARD_SLASH + stopNumber + FORWARD_SLASH + SCHEDULE_PARAMETER + QUESTION_MARK + startTimeFilter + endTimeFilter + routeFilter + USAGE + API_KEY;
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
            return new SearchQuery(search, API_URL + STOPS_PARAMETER + COLON + createURLFriendlyString(search) + QUESTION_MARK + API_KEY_PARAMETER + API_KEY, SearchQueryTypeIds.GENERAL.searchQueryTypeId);
        }
    }

    public static SearchQuery generateSearchQuery(int routeNumber) {
        return new SearchQuery(Integer.toString(routeNumber), API_URL + STOPS_PARAMETER + QUESTION_MARK + ROUTE_PARAMETER + routeNumber + AMPERSAND + API_KEY_PARAMETER + API_KEY, SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId);
    }

    public static SearchQuery generateSearchQuery(RouteKey key) {
        return new SearchQuery(key.getKeyString(), API_URL + STOPS_PARAMETER + QUESTION_MARK + VARIANT_PARAMETER + key.getKeyString() + AMPERSAND + API_KEY_PARAMETER + API_KEY, SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId);
    }

    public static String generateStopFeaturesUrl(int stopNumber) {
        return API_URL + STOPS_PARAMETER + FORWARD_SLASH + Integer.toString(stopNumber) + FORWARD_SLASH + STOP_FEATURE_PARAMETER + QUESTION_MARK + API_KEY_PARAMETER + API_KEY;
    }

    private static String createURLFriendlyString(String s) {
        String[] words = s.split("\\s+");
        String urlFriendlyString = words[0];

        for(int i = 1; i < words.length; i++)
            urlFriendlyString += "+" + words[i];

        return urlFriendlyString;
    }

    public static int getCoverageTypeId(String coverageType) {
        if(coverageType.equals(CoverageTypes.EXPRESS.typeName))
            return CoverageTypes.EXPRESS.typeId;
        else if(coverageType.equals(CoverageTypes.SUPER_EXPRESS.typeName))
            return CoverageTypes.SUPER_EXPRESS.typeId;
        else if(coverageType.equals(CoverageTypes.RAPID_TRANSIT.typeName))
            return CoverageTypes.RAPID_TRANSIT.typeId;
        else
            return CoverageTypes.REGULAR.typeId;
    }
}