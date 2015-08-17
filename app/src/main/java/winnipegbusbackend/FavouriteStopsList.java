package winnipegbusbackend;

import android.util.Xml;

import com.kieran.winnipegbus.HomeScreenActivity;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FavouriteStopsList {
    static String FILES_DIR = HomeScreenActivity.filesDir + "/favourites.xml";
    public static List<FavouriteStop> favouritesList = new ArrayList<>();

    public static void addToFavourites(FavouriteStop favouriteStop) {
        if(!contains(favouriteStop)) {
            favouritesList.add(favouriteStop);
            saveFavouriteStops();
        }
    }

    public static boolean contains(FavouriteStop favouriteStop) {
        for(FavouriteStop fs : favouritesList)
            if(fs.getStopNumber() == favouriteStop.getStopNumber())
                return true;

        return false;
    }

    public static boolean contains(int stopNumber) {
        for(FavouriteStop fs : favouritesList)
            if(fs.getStopNumber() == stopNumber)
                return true;

        return false;
    }

    public static void removeFromFavourites(int stopNumber) {
        favouritesList.remove(getFavouriteStopByStopNumber(stopNumber));
        saveFavouriteStops();
    }

    public static FavouriteStop getFavouriteStopByStopNumber(int stopNumber) {
        for(FavouriteStop fs : favouritesList)
            if(fs.getStopNumber() == stopNumber)
                return fs;
        return null;
    }

   public static void loadFavourites() {
        BusUtilities utilities = new BusUtilities();

        try {
            Document XMLDocument = utilities.getXML(new FileInputStream(FILES_DIR));
            NodeList stopNumbers = XMLDocument.getElementsByTagName(FavouritesNodeTags.STOP_NUMBER.tag);
            NodeList stopNames = XMLDocument.getElementsByTagName(FavouritesNodeTags.STOP_NAME.tag);
            NodeList timesUsed = XMLDocument.getElementsByTagName(FavouritesNodeTags.TIMES_USED.tag);

            for (int r = 0; r < stopNumbers.getLength(); r++)
                addToFavourites(new FavouriteStop(stopNames.item(r).getFirstChild().getNodeValue(), Integer.parseInt(stopNumbers.item(r).getFirstChild().getNodeValue()), Integer.parseInt(timesUsed.item(r).getFirstChild().getNodeValue())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean saveFavouriteStops() {
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(FILES_DIR);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startTag("", FavouritesNodeTags.FAVOURITE_STOPS.tag);

            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            for (int i = 0; i < favouritesList.size(); i++) {
                serializer.startTag("", FavouritesNodeTags.FAVOURITE_STOP.tag);
                serializer.startTag("", FavouritesNodeTags.STOP_NUMBER.tag);
                serializer.text(Integer.toString(favouritesList.get(i).getStopNumber()));
                serializer.endTag("", FavouritesNodeTags.STOP_NUMBER.tag);

                serializer.startTag("", FavouritesNodeTags.STOP_NAME.tag);
                serializer.text(favouritesList.get(i).getStopName());
                serializer.endTag("", FavouritesNodeTags.STOP_NAME.tag);

                serializer.startTag("", FavouritesNodeTags.TIMES_USED.tag);
                serializer.text(Integer.toString(favouritesList.get(i).getTimesUsed()));
                serializer.endTag("", FavouritesNodeTags.TIMES_USED.tag);

                serializer.endTag("", FavouritesNodeTags.FAVOURITE_STOP.tag);
            }
            serializer.endTag("", FavouritesNodeTags.FAVOURITE_STOPS.tag);
            serializer.endDocument();
            serializer.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static List<FavouriteStop> getFavouriteStopsSorted(final int sortTypeId) {
        List<FavouriteStop> favouritesList = FavouriteStopsList.favouritesList;
        List<FavouriteStop> sortedFavouritesList = new ArrayList<>();

        for (FavouriteStop f : favouritesList)
            sortedFavouritesList.add(FavouriteStop.clone(f));

        Collections.sort(sortedFavouritesList, new Comparator<FavouriteStop>() {
            @Override
            public int compare(FavouriteStop stop1, FavouriteStop stop2) {
                if(sortTypeId == FavouritesListSortTypeId.SAVED_INDEX.value)
                    return 0;
                else if(sortTypeId == FavouritesListSortTypeId.STOP_NUBMER_ASC.value)
                    return stop1.getStopNumber() - stop2.getStopNumber();
                else if(sortTypeId == FavouritesListSortTypeId.STOP_NUBMER_DESC.value)
                    return -(stop1.getStopNumber() - stop2.getStopNumber());
                else if(sortTypeId == FavouritesListSortTypeId.FREQUENCY_ASC.value)
                    return (stop1.getTimesUsed() - stop2.getTimesUsed());
                else if(sortTypeId == FavouritesListSortTypeId.FREQUENCY_DESC.value)
                    return -(stop1.getTimesUsed() - stop2.getTimesUsed());
                return 0;
            }
        });

        return sortedFavouritesList;
    }
}
