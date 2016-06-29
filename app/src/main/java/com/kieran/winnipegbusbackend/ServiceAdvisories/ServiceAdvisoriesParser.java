package com.kieran.winnipegbusbackend.ServiceAdvisories;

import com.kieran.winnipegbusbackend.BusUtilities;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
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

        String[] affectedStops = bodySections[1].split("^\\*\\ ");


        return new ServiceAdvisory(title, null, BusUtilities.convertToDate(BusUtilities.getValue("updated-at", node)));
    }
}
