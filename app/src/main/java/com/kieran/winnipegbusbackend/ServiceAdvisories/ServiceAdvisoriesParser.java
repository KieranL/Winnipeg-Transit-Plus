package com.kieran.winnipegbusbackend.ServiceAdvisories;

import com.kieran.winnipegbusbackend.BusUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServiceAdvisoriesParser {
    public static List<ServiceAdvisory> parseAdvisories(Document document) {
        List<ServiceAdvisory> advisories = new ArrayList<>();
        NodeList nodes =  document.getElementsByTagName("service-advisory");

        for (int i = 0; i < nodes.getLength(); i++)
            advisories.add(getServiceAdvisory(nodes.item(i)));

        return advisories;
    }

    private static ServiceAdvisory getServiceAdvisory(Node node) {
        String title = BusUtilities.getValue("title", node);
        String body = BusUtilities.getValue("body", node);
        String[] bodySections = body.split("\\n\\n");
        String header = bodySections[0];
        String[] affectedStopsData;
        String[] reRoutesData;

        if(bodySections.length == 3) {
            affectedStopsData = new String[]{};
            reRoutesData = bodySections[2].split("[^\\*]\\*{1} ");
        }else {
            affectedStopsData = bodySections[1].split("[^\\*]\\*{1} ");
            reRoutesData = bodySections[3].split("[^\\*]\\*{1} ");
        }

        return new ServiceAdvisory(title, header, getAffectedStops(affectedStopsData), getReRoutes(reRoutesData), BusUtilities.convertToDate(BusUtilities.getValue("updated-at", node)));
    }

    private static List<AffectedStop> getAffectedStops(String[] nodes) {
        List<AffectedStop> stops = new ArrayList<>();

        for (String node : nodes) {
            String[] s = node.split("\\*\\*");

            if(s.length > 1)
                stops.add(new AffectedStop(s[0].replaceAll("\\*", "").trim(), s[1]));
            else
                stops.add(new AffectedStop(s[0].replaceAll("\\*", "").trim(), null));
        }

        return stops;
    }

    private static List<Reroute> getReRoutes(String[] nodes) {
        List<Reroute> reroutes = new ArrayList<>();

        for (String node : nodes) {
            String[] s = node.split("\n\\*\\*");
            String[] sub = new String[s.length - 1];
            System.arraycopy(s, 1, sub, 0, s.length -1);

            reroutes.add(new Reroute(s[0].replaceAll("\\*", "").trim(), Arrays.asList(sub)));
        }

        return reroutes;
    }
}
