package winnipegbusbackend;

import android.content.Context;
import android.util.Xml;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FavouriteStopsList {
    static String filesDir = "/data/data/com.kieran.winnipegbus/files/favourites.xml";
    public static List<Integer> favouritesList = new ArrayList<Integer>();

    public static void addToFavourites(int stopNumber) {
        if(!favouritesList.contains(stopNumber))
            favouritesList.add(stopNumber);
    }

    public static void removeFromFavourites(int stopNumber) {
        favouritesList.remove(favouritesList.indexOf(stopNumber));
    }

   public static void loadFavourites() {
        BusUtilities utilities = new BusUtilities();

        try {
            Document XMLDocument = utilities.getXML(new FileInputStream(filesDir));
            NodeList stopNumbers = XMLDocument.getElementsByTagName("stopNumber");

            for (int r = 0; r < stopNumbers.getLength(); r++)
                addToFavourites(Integer.parseInt(stopNumbers.item(r).getFirstChild().getNodeValue()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveFavouriteStops() {
        try {
            FileOutputStream fos; // = openFileOutput("favourites.xml", Context.MODE_PRIVATE);
            fos = new FileOutputStream(filesDir);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startTag("", "stopNumbers");

            for (int i = 0; i < favouritesList.size(); i++) {
                serializer.startTag("", "stopNumber");
                serializer.text(Integer.toString(favouritesList.get(i)));
                serializer.endTag("", "stopNumber");
            }
            serializer.endTag("", "stopNumbers");
            serializer.endDocument();
            serializer.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
