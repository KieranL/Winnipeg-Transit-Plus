package com.kieran.winnipegbusbackend;

import android.util.Xml;

import com.kieran.winnipegbus.Activities.BaseActivity;
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType;
import com.kieran.winnipegbusbackend.enums.FavouritesNodeTags;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
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
    private final static String FILES_DIR = BaseActivity.filesDir + "/favourites.xml";
    private static List<FavouriteStop> favouritesList = new ArrayList<>();
    public static boolean isLoadNeeded = true;
    private final static String XMLFeature = "http://xmlpull.org/v1/doc/features.html#indent-output";

    public static void addToFavourites(FavouriteStop favouriteStop) {
        if(!contains(favouriteStop)) {
            favouritesList.add(favouriteStop);
            saveFavouriteStops();
        }
    }

    public static boolean contains(FavouriteStop favouriteStop) {
       return contains(favouriteStop.getNumber());
    }

    public static boolean contains(int stopNumber) {
        for(FavouriteStop fs : favouritesList)
            if(fs.getNumber() == stopNumber)
                return true;

        return false;
    }

    public static void removeFromFavourites(int stopNumber) {
        favouritesList.remove(getFavouriteStopByStopNumber(stopNumber));
        saveFavouriteStops();
    }

    public static FavouriteStop getFavouriteStopByStopNumber(int stopNumber) {
        for(FavouriteStop fs : favouritesList)
            if(fs.getNumber() == stopNumber)
                return fs;
        return null;
    }

   public static boolean loadFavourites() {
       if(favouritesList.isEmpty())
           isLoadNeeded = true;
       if(isLoadNeeded) {
           try {
               Document XMLDocument = (Document)BusUtilities.getXML(new FileInputStream(FILES_DIR)).getResult();
               NodeList favouriteStops = XMLDocument.getElementsByTagName(FavouritesNodeTags.FAVOURITE_STOP.tag);

               for (int r = 0; r < favouriteStops.getLength(); r++) {
                   Node curr = favouriteStops.item(r);
                   int stopNumber = Integer.parseInt(BusUtilities.getValue(FavouritesNodeTags.STOP_NUMBER.tag, curr));
                   String stopName = BusUtilities.getValue(FavouritesNodeTags.STOP_NAME.tag, favouriteStops.item(r));
                   int timesUsed = Integer.parseInt(BusUtilities.getValue(FavouritesNodeTags.TIMES_USED.tag, curr));
                   String alias = BusUtilities.getValue(FavouritesNodeTags.ALIAS.tag, favouriteStops.item(r));

                   FavouriteStop favouriteStop = new FavouriteStop(stopName, stopNumber, timesUsed);
                    if(alias != null)
                        favouriteStop.setAlias(alias);

                   if(!contains(favouriteStop))
                        favouritesList.add(favouriteStop);
               }
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
                try {
                    FavouriteStop favouriteStop = favouritesList.get(i);
                    serializer.startTag("", FavouritesNodeTags.FAVOURITE_STOP.tag);
                    serializer.startTag("", FavouritesNodeTags.STOP_NUMBER.tag);
                    serializer.text(Integer.toString(favouritesList.get(i).getNumber()));
                    serializer.endTag("", FavouritesNodeTags.STOP_NUMBER.tag);

                    serializer.startTag("", FavouritesNodeTags.STOP_NAME.tag);
                    serializer.text(favouriteStop.getName());
                    serializer.endTag("", FavouritesNodeTags.STOP_NAME.tag);

                    if (favouriteStop.getAlias() != null) {
                        serializer.startTag("", FavouritesNodeTags.ALIAS.tag);
                        serializer.text(favouriteStop.getAlias());
                        serializer.endTag("", FavouritesNodeTags.ALIAS.tag);
                    }

                    serializer.startTag("", FavouritesNodeTags.TIMES_USED.tag);
                    serializer.text(Integer.toString(favouriteStop.getTimesUsed()));
                    serializer.endTag("", FavouritesNodeTags.TIMES_USED.tag);

                    serializer.endTag("", FavouritesNodeTags.FAVOURITE_STOP.tag);
                }catch (Exception e) {
                    //intentionally blank
                }
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

    public static void sort(final FavouritesListSortType sortType) {
        Collections.sort(favouritesList, new Comparator<FavouriteStop>() {
            @Override
            public int compare(FavouriteStop stop1, FavouriteStop stop2) {
                switch (sortType) {
                    case STOP_NUMBER_ASC:
                        return stop1.getNumber() - stop2.getNumber();
                    case STOP_NUMBER_DESC:
                        return -(stop1.getNumber() - stop2.getNumber());
                    case FREQUENCY_ASC:
                        return (stop1.getTimesUsed() - stop2.getTimesUsed());
                    case FREQUENCY_DESC:
                        return -(stop1.getTimesUsed() - stop2.getTimesUsed());
                    default:
                        return stop1.getNumber() - stop2.getNumber();
                }
            }
        });
    }

    public static List<FavouriteStop> getFavouriteStopsSorted(final FavouritesListSortType sortType) {
        loadFavourites();

        sort(sortType);
        return favouritesList;
    }

    public static FavouriteStop get(int position) {
        return favouritesList.get(position);
    }
}
