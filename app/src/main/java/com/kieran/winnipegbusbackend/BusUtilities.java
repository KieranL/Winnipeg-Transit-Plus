package com.kieran.winnipegbusbackend;

import com.kieran.winnipegbus.SearchQuery;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class BusUtilities {
    private final static String API_KEY = "FTy2QN8ts293ZlhYP1t";
    private final static String API_URL = "http://api.winnipegtransit.com/v2/";
    private final static String USAGE = "usage=short&api-key=";
    private final static String DATE_FORMAT = "yyyy-MM-dd-HH:mm:ss";

    public StopTime convertToDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);

        try {
            return new StopTime(format.parse(s.replaceFirst("T", "-")));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValue(String tag, Element element) {
        try {
            Node node = element.getElementsByTagName(tag).item(0).getFirstChild();
            return node.getNodeValue();
        }catch (Exception e) {
            return null;
        }
    }

    public LoadResult getXML(String path) {
        try {
            return getXML(new URL(path).openStream());
        } catch (IOException e) {
            return new LoadResult(null, e);
        }
    }

    public LoadResult getXML(InputStream inputStream)  {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document XMLDocument = db.parse(inputStream);
            XMLDocument.getDocumentElement().normalize();
            XMLDocument.normalizeDocument();
            return new LoadResult(XMLDocument, null);
        } catch (Exception e) {
            return new LoadResult(null, e);
        }
    }

    public String generateStopNumberURL(int s, int[] routeNumbers) {
        String routeFilter = "";

        if (routeNumbers != null) {
            String routes = "";
            for (int i = 0; i < routeNumbers.length; i++) {
                routes += Integer.toString(routeNumbers[i]);
                if (i < routeNumbers.length - 1)
                    routes += ",";
            }
            routeFilter = ("route=" + routes + "&");
        }
        return API_URL + "stops/" + s + "/schedule?"+ routeFilter + USAGE + API_KEY;
    }

    public SearchQuery generateSearchQuery(String search) {
        try{
            int routeNumber = Integer.parseInt(search);
            return new SearchQuery(search, API_URL + "stops?route=" + routeNumber + "&api-key=" + API_KEY, SearchQueryTypeIds.ROUTE_NUMBER.searchQueryTypeId);
        }catch (Exception e) {
            return new SearchQuery(search, API_URL + "stops:" + createURLFriendlyString(search) + "?api-key=" + API_KEY, SearchQueryTypeIds.GENERAL.searchQueryTypeId);
        }
    }

    private String createURLFriendlyString(String s) {
        String[] words = s.split(" ");
        String urlFriendlyString = words[0];

        for(int i = 1; i < words.length; i++)
            urlFriendlyString+= "+" + words[i];

        return urlFriendlyString;
    }

    public int[] getIntegerArrayFromString(String s) {
        int[] routeNumbers;

        try {
            String[] routeNumberString = s.split(" ");
            routeNumbers = new int[routeNumberString.length];

            for (int i = 0; i < routeNumberString.length; i++)
                routeNumbers[i] = Integer.parseInt(routeNumberString[i]);
        } catch (Exception e) {
            return null;
        }

        return routeNumbers;
    }

    public int getCoverageTypeId(String coverageType) {
        if(coverageType.equals(CoverageTypes.EXPRESS.typeName))
            return CoverageTypes.EXPRESS.typeId;
        else if(coverageType.equals(CoverageTypes.RAPID_TRANSIT.typeName))
            return CoverageTypes.RAPID_TRANSIT.typeId;
        else
            return CoverageTypes.REGULAR.typeId;
    }
}

