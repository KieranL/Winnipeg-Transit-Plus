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

    public static String generateStopNumberURL(int s, List<Integer> routeNumbers, StopTime endTime) {
        String routeFilter = "";

        if (routeNumbers != null) {
            String routes = "";
            for (int i = 0; i < routeNumbers.size(); i++) {
                routes += Integer.toString(routeNumbers.get(i));
                if (i < routeNumbers.size() - 1)
                    routes += ",";
            }
            routeFilter = (ROUTE_PARAMETER + routes + "&");
        }
        return API_URL + "stops/" + s + "/schedule?" + END_TIME_PARAMETER + endTime.to24hrTimeString() + "&"+ routeFilter + USAGE + API_KEY;
    }

    public static SearchQuery generateSearchQuery(String search) {
        try{
            int routeNumber = Integer.parseInt(search);
            return new SearchQuery(search, API_URL + "stops?" + ROUTE_PARAMETER + routeNumber + "&api-key=" + API_KEY, SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId);
        }catch (Exception e) {
            return new SearchQuery(search, API_URL + "stops:" + createURLFriendlyString(search) + "?api-key=" + API_KEY, SearchQueryTypeIds.GENERAL.searchQueryTypeId);
        }
    }

    private static String createURLFriendlyString(String s) {
        String[] words = s.split(" ");
        String urlFriendlyString = words[0];

        for(int i = 1; i < words.length; i++)
            urlFriendlyString+= "+" + words[i];

        return urlFriendlyString;
    }

    public static int getCoverageTypeId(String coverageType) {
        if(coverageType.equals(CoverageTypes.EXPRESS.typeName))
            return CoverageTypes.EXPRESS.typeId;
        else if(coverageType.equals(CoverageTypes.RAPID_TRANSIT.typeName))
            return CoverageTypes.RAPID_TRANSIT.typeId;
        else
            return CoverageTypes.REGULAR.typeId;
    }
}

