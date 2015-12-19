package com.kieran.winnipegbusbackend;

import android.util.Xml;

import com.kieran.winnipegbus.Activities.HomeScreenActivity;
import com.kieran.winnipegbusbackend.enums.FavouritesListSortTypeIds;
import com.kieran.winnipegbusbackend.enums.FavouritesNodeTags;

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
    private final static String FILES_DIR = HomeScreenActivity.filesDir + "/favourites.xml";
    public static List<FavouriteStop> favouritesList = new ArrayList<>();
    public static boolean isLoadNeeded = true;
    private final static String XMLFeature = "http://xmlpull.org/v1/doc/features.html#indent-output";

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

   public static boolean loadFavourites() {
       if(favouritesList.size() > 0)
           isLoadNeeded = true;
       if(isLoadNeeded) {
           try {
               Document XMLDocument = (Document)BusUtilities.getXML(new FileInputStream(FILES_DIR)).getResult();
               NodeList stopNumbers = XMLDocument.getElementsByTagName(FavouritesNodeTags.STOP_NUMBER.tag);
               NodeList stopNames = XMLDocument.getElementsByTagName(FavouritesNodeTags.STOP_NAME.tag);
               NodeList timesUsed = XMLDocument.getElementsByTagName(FavouritesNodeTags.TIMES_USED.tag);

               for (int r = 0; r < stopNumbers.getLength(); r++)
                   addToFavourites(new FavouriteStop(stopNames.item(r).getFirstChild().getNodeValue(), Integer.parseInt(stopNumbers.item(r).getFirstChild().getNodeValue()), Integer.parseInt(timesUsed.item(r).getFirstChild().getNodeValue())));

               isLoadNeeded = false;
           } catch (Exception e) {
               isLoadNeeded = true;
           }
       }
        return isLoadNeeded;
    }

    public static boolean saveFavouriteStops() {
        isLoadNeeded = true;
        try {
            FileOutputStream fos;
            fos = new FileOutputStream(FILES_DIR);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "UTF-8");
            serializer.startTag("", FavouritesNodeTags.FAVOURITE_STOPS.tag);
            serializer.setFeature(XMLFeature, true);

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
        loadFavourites();
        List<FavouriteStop> sortedFavouritesList = new ArrayList<>();

        for (FavouriteStop f : favouritesList)
            sortedFavouritesList.add(FavouriteStop.clone(f));

        Collections.sort(sortedFavouritesList, new Comparator<FavouriteStop>() {
            @Override
            public int compare(FavouriteStop stop1, FavouriteStop stop2) {
                if(sortTypeId == FavouritesListSortTypeIds.SAVED_INDEX.value)
                    return 0;
                else if(sortTypeId == FavouritesListSortTypeIds.STOP_NUMBER_ASC.value)
                    return stop1.getStopNumber() - stop2.getStopNumber();
                else if(sortTypeId == FavouritesListSortTypeIds.STOP_NUMBER_DESC.value)
                    return -(stop1.getStopNumber() - stop2.getStopNumber());
                else if(sortTypeId == FavouritesListSortTypeIds.FREQUENCY_ASC.value)
                    return (stop1.getTimesUsed() - stop2.getTimesUsed());
                else if(sortTypeId == FavouritesListSortTypeIds.FREQUENCY_DESC.value)
                    return -(stop1.getTimesUsed() - stop2.getTimesUsed());
                return 0;
            }
        });

        return sortedFavouritesList;
    }

    public static int length() {
        return favouritesList.size();
    }
}
