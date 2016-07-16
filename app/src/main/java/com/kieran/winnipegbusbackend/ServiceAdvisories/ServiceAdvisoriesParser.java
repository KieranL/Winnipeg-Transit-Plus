package com.kieran.winnipegbusbackend.ServiceAdvisories;

import com.kieran.winnipegbusbackend.BusUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceAdvisoriesParser {

    public static final String BODY_SECTIONS_REGEX = "\\n\\n";
    public static final String BODY_SUBSECTION_REGEX = "[^\\*]\\*{1} ";
    public static final String REMOVE_ASTERISK_REGEX = "\\*";
    public static final String TITLE_TAG = "title";
    public static final String BODY_TAG = "body";
    public static final String SERVICE_ADVISORY_TAG = "service-advisory";
    public static final String AFFECTED_STOP_REGEX = "\\*\\*";
    public static final String REROUTE_REGEX = "\n\\*\\*";

    public static List<ServiceAdvisory> parseAdvisories(Document document) {
        List<ServiceAdvisory> advisories = new ArrayList<>();
        NodeList nodes =  document.getElementsByTagName(SERVICE_ADVISORY_TAG);

        for (int i = 0; i < nodes.getLength(); i++)
            advisories.add(getServiceAdvisory(nodes.item(i)));

        Collections.sort(advisories);
        return advisories;
    }

    private static ServiceAdvisory getServiceAdvisory(Node node) {
        String title = BusUtilities.getValue(TITLE_TAG, node);
        String body = BusUtilities.getValue(BODY_TAG, node);
        String[] bodySections = body.split(BODY_SECTIONS_REGEX);
        String header = bodySections[0];
        String[] affectedStopsData;
        String[] reRoutesData;

        if(bodySections.length == 3) {
            affectedStopsData = new String[]{};
            reRoutesData = bodySections[2].split(BODY_SUBSECTION_REGEX);
        }else {
            affectedStopsData = bodySections[1].split(BODY_SUBSECTION_REGEX);
            reRoutesData = bodySections[3].split(BODY_SUBSECTION_REGEX);
        }

        return new ServiceAdvisory(title, header, getAffectedStops(affectedStopsData), getReRoutes(reRoutesData), BusUtilities.convertToStopTime(BusUtilities.getValue("updated-at", node)));
    }

    private static List<AffectedStop> getAffectedStops(String[] nodes) {
        List<AffectedStop> stops = new ArrayList<>();

        for (String node : nodes) {
            String[] s = node.split(AFFECTED_STOP_REGEX);

            if(s.length > 1)
                stops.add(new AffectedStop(s[0].replaceAll(REMOVE_ASTERISK_REGEX, "").trim(), s[1]));
            else
                stops.add(new AffectedStop(s[0].replaceAll(REMOVE_ASTERISK_REGEX, "").trim(), null));
        }

        return stops;
    }

    private static List<Reroute> getReRoutes(String[] nodes) {
        List<Reroute> reroutes = new ArrayList<>();

        for (String node : nodes) {
            String[] s = node.split(REROUTE_REGEX);
            String[] sub = new String[s.length - 1];
            System.arraycopy(s, 1, sub, 0, s.length -1);

            reroutes.add(new Reroute(s[0].replaceAll(REMOVE_ASTERISK_REGEX, "").trim(), Arrays.asList(sub)));
        }

        return reroutes;
    }
}
