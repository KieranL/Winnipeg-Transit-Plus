package winnipegbusbackend;

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

    public StopTime convertToDate(String s) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

        try {
            return new StopTime(format.parse(s.replaceFirst("T", "-")));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getValue(String tag, Element element) {
        Node node = element.getElementsByTagName(tag).item(0).getFirstChild();
        return node.getNodeValue();
    }

    public Document getXML(String path) {
        try {
            return getXML(new URL(path).openStream());
        } catch (IOException e) {
            return null;
        }
    }

    public Document getXML(InputStream inputStream) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document XMLDocument = db.parse(inputStream);
            XMLDocument.getDocumentElement().normalize();
            XMLDocument.normalizeDocument();
            return XMLDocument;
        } catch (Exception e) {
            return null;
        }
    }

    public String generateURL(String s, int[] routeNumbers) {
        String routeFilter = "";

        if (routeNumbers[0] != 0) {
            String routes = "";
            for (int i = 0; i < routeNumbers.length; i++) {
                routes += Integer.toString(routeNumbers[i]);
                if (i < routeNumbers.length - 1)
                    routes += ",";
            }
            routeFilter = ("route=" + routes + "&");
        }
        return API_URL + s + routeFilter + USAGE + API_KEY;
    }
}

