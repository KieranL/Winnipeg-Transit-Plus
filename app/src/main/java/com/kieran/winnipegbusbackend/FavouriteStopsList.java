package com.kieran.winnipegbusbackend;

import android.util.Xml;

import com.kieran.winnipegbus.Activities.BaseActivity;
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FavouriteStopsList {
    private final static String FILES_DIR = BaseActivity.filesDir + "/favourites.xml";

    private final static String FAVOURITE_STOPS_TAG = "favouriteStops";
    private final static String FAVOURITE_STOP_TAG = "favouriteStop";
    private final static String STOP_NUMBER_TAG =  "stopNumber";
    private final static String STOP_NAME_TAG = "stopName";
    private final static String TIMES_USED_TAG = "timesUsed";
    private final static String ALIAS_TAG = "alias";

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

    public static void remove(int stopNumber) {
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
               Document XMLDocument = getXML(new FileInputStream(FILES_DIR)).getResult();
               NodeList favouriteStops = XMLDocument.getElementsByTagName(FAVOURITE_STOP_TAG);

               for (int r = 0; r < favouriteStops.getLength(); r++) {
                   Node curr = favouriteStops.item(r);
                   int stopNumber = Integer.parseInt(getValue(STOP_NUMBER_TAG, curr));
                   String stopName = getValue(STOP_NAME_TAG, favouriteStops.item(r));
                   int timesUsed = Integer.parseInt(getValue(TIMES_USED_TAG, curr));
                   String alias = getValue(ALIAS_TAG, favouriteStops.item(r));

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
            serializer.startTag("", FAVOURITE_STOPS_TAG);
            serializer.setFeature(XMLFeature, true);

            for (int i = 0; i < favouritesList.size(); i++) {
                try {
                    FavouriteStop favouriteStop = favouritesList.get(i);
                    serializer.startTag("", FAVOURITE_STOP_TAG);
                    serializer.startTag("", STOP_NUMBER_TAG);
                    serializer.text(Integer.toString(favouritesList.get(i).getNumber()));
                    serializer.endTag("", STOP_NUMBER_TAG);

                    serializer.startTag("", STOP_NAME_TAG);
                    serializer.text(favouriteStop.getName());
                    serializer.endTag("", STOP_NAME_TAG);

                    if (favouriteStop.getAlias() != null) {
                        serializer.startTag("", ALIAS_TAG);
                        serializer.text(favouriteStop.getAlias());
                        serializer.endTag("", ALIAS_TAG);
                    }

                    serializer.startTag("", TIMES_USED_TAG);
                    serializer.text(Integer.toString(favouriteStop.getTimesUsed()));
                    serializer.endTag("", TIMES_USED_TAG);

                    serializer.endTag("", FAVOURITE_STOP_TAG);
                }catch (Exception e) {
                    //intentionally blank
                }
            }
            serializer.endTag("", FAVOURITE_STOPS_TAG);
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

    private static String getValue(String tag, Node originalNode) {
        try {
            Node node = ((Element)originalNode).getElementsByTagName(tag).item(0).getFirstChild();
            return node.getNodeValue();
        }catch (Exception e) {
            return null;
        }
    }

    private static LoadResult<Document> getXML(InputStream inputStream)  {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document XMLDocument = db.parse(inputStream);

            return new LoadResult<>(XMLDocument, null);
        } catch (Exception e) {
            return new LoadResult<>(null, e);
        }
    }
}
