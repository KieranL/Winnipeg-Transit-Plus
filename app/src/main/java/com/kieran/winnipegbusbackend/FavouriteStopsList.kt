package com.kieran.winnipegbusbackend

import android.util.Xml

import com.kieran.winnipegbus.activities.BaseActivity
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.LoadResult
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Collections

import javax.xml.parsers.DocumentBuilderFactory

object FavouriteStopsList {
    private val FILES_DIR = BaseActivity.filesDirPath!! + "/favourites.xml"

    private val FAVOURITE_STOPS_TAG = "favouriteStops"
    private val FAVOURITE_STOP_TAG = "favouriteStop"
    private val STOP_NUMBER_TAG = "stopNumber"
    private val STOP_NAME_TAG = "stopName"
    private val TIMES_USED_TAG = "timesUsed"
    private val ALIAS_TAG = "alias"

    private val favouritesList = ArrayList<FavouriteStop>()
    var isLoadNeeded = true
    private val XMLFeature = "http://xmlpull.org/v1/doc/features.html#indent-output"

    fun addToFavourites(favouriteStop: FavouriteStop) {
        if (!contains(favouriteStop)) {
            favouritesList.add(favouriteStop)
            saveFavouriteStops()
        }
    }

    operator fun contains(favouriteStop: FavouriteStop): Boolean {
        return contains(favouriteStop.number)
    }

    operator fun contains(stopNumber: Int): Boolean {
        return favouritesList.any { it.number == stopNumber }
    }

    fun remove(stopNumber: Int) {
        favouritesList.remove(getFavouriteStopByStopNumber(stopNumber))
        saveFavouriteStops()
    }

    fun getFavouriteStopByStopNumber(stopNumber: Int): FavouriteStop? {
        return favouritesList.firstOrNull { it.number == stopNumber }
    }

    fun loadFavourites(): Boolean {
        if (favouritesList.isEmpty())
            isLoadNeeded = true
        if (isLoadNeeded) {
            try {
                val XMLDocument = getXML(FileInputStream(FILES_DIR)).result
                val favouriteStops = XMLDocument!!.getElementsByTagName(FAVOURITE_STOP_TAG)

                for (r in 0 until favouriteStops.length) {
                    val curr = favouriteStops.item(r)
                    val stopNumber = Integer.parseInt(getValue(STOP_NUMBER_TAG, curr))
                    val stopName = getValue(STOP_NAME_TAG, favouriteStops.item(r))
                    val timesUsed = Integer.parseInt(getValue(TIMES_USED_TAG, curr))
                    val alias = getValue(ALIAS_TAG, favouriteStops.item(r))

                    val favouriteStop = FavouriteStop(stopName!!, stopNumber, timesUsed)
                    if (alias != null)
                        favouriteStop.alias = alias

                    if (!contains(favouriteStop))
                        favouritesList.add(favouriteStop)
                }
                isLoadNeeded = false
            } catch (e: Exception) {
                isLoadNeeded = true
            }

        }
        return isLoadNeeded
    }

    fun saveFavouriteStops(): Boolean {
        isLoadNeeded = true
        try {
            val fos = FileOutputStream(FILES_DIR)
            val serializer = Xml.newSerializer()
            serializer.setOutput(fos, "UTF-8")
            serializer.startTag("", FAVOURITE_STOPS_TAG)
            serializer.setFeature(XMLFeature, true)

            for (i in favouritesList.indices) {
                try {
                    val favouriteStop = favouritesList[i]
                    serializer.startTag("", FAVOURITE_STOP_TAG)
                    serializer.startTag("", STOP_NUMBER_TAG)
                    serializer.text(Integer.toString(favouritesList[i].number))
                    serializer.endTag("", STOP_NUMBER_TAG)

                    serializer.startTag("", STOP_NAME_TAG)
                    serializer.text(favouriteStop.name)
                    serializer.endTag("", STOP_NAME_TAG)

                    if (favouriteStop.alias != null) {
                        serializer.startTag("", ALIAS_TAG)
                        serializer.text(favouriteStop.alias)
                        serializer.endTag("", ALIAS_TAG)
                    }

                    serializer.startTag("", TIMES_USED_TAG)
                    serializer.text(Integer.toString(favouriteStop.timesUsed))
                    serializer.endTag("", TIMES_USED_TAG)

                    serializer.endTag("", FAVOURITE_STOP_TAG)
                } catch (e: Exception) {
                    //intentionally blank
                }

            }
            serializer.endTag("", FAVOURITE_STOPS_TAG)
            serializer.endDocument()
            serializer.flush()
            fos.close()
            return true
        } catch (e: IOException) {
            return false
        }

    }

    fun sort(sortType: FavouritesListSortType) {
        Collections.sort(favouritesList) { stop1, stop2 ->
            when (sortType) {
                FavouritesListSortType.STOP_NUMBER_ASC -> stop1.number - stop2.number
                FavouritesListSortType.STOP_NUMBER_DESC -> -(stop1.number - stop2.number)
                FavouritesListSortType.FREQUENCY_ASC -> stop1.timesUsed - stop2.timesUsed
                FavouritesListSortType.FREQUENCY_DESC -> -(stop1.timesUsed - stop2.timesUsed)
            }
        }
    }

    fun getFavouriteStopsSorted(sortType: FavouritesListSortType): List<FavouriteStop> {
        loadFavourites()

        sort(sortType)
        return favouritesList
    }

    operator fun get(position: Int): FavouriteStop {
        return favouritesList[position]
    }

    private fun getValue(tag: String, originalNode: Node): String? {
        return try {
            val node = (originalNode as Element).getElementsByTagName(tag).item(0).firstChild
            node.nodeValue
        } catch (e: Exception) {
            null
        }

    }

    private fun getXML(inputStream: InputStream): LoadResult<Document> {
        return try {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val XMLDocument = db.parse(inputStream)

            LoadResult(XMLDocument, null)
        } catch (e: Exception) {
            LoadResult<Document>(null, e)
        }

    }
}
