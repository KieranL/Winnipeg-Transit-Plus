package com.kieran.winnipegbusbackend.agency.winnipegtransit

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
import java.util.*
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

    operator fun contains(favouriteStop: FavouriteStop): Boolean {
        return contains((favouriteStop.identifier as WinnipegTransitStopIdentifier).stopNumber)
    }

    operator fun contains(stopNumber: Int): Boolean {
        return favouritesList.any { (it.identifier as WinnipegTransitStopIdentifier).stopNumber == stopNumber }
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

                    val favouriteStop = FavouriteStop(stopName!!, WinnipegTransitStopIdentifier(stopNumber), timesUsed)
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

    fun sort(sortType: FavouritesListSortType) {
        Collections.sort(favouritesList) { stop1, stop2 ->
            when (sortType) {
                FavouritesListSortType.STOP_NUMBER_ASC -> (stop1.identifier as WinnipegTransitStopIdentifier).stopNumber - (stop2.identifier as WinnipegTransitStopIdentifier).stopNumber
                FavouritesListSortType.STOP_NUMBER_DESC -> -((stop1.identifier as WinnipegTransitStopIdentifier).stopNumber - (stop2.identifier as WinnipegTransitStopIdentifier).stopNumber)
                FavouritesListSortType.FREQUENCY_ASC -> stop1.timesUsed - stop2.timesUsed
                FavouritesListSortType.FREQUENCY_DESC -> -(stop1.timesUsed - stop2.timesUsed)
            }
        }
    }

    fun getFavouriteStopsSorted(sortType: FavouritesListSortType, maxItems: Int = -1): List<FavouriteStop> {
        loadFavourites()

        sort(sortType)

        return if(maxItems == -1) {
            favouritesList
        }else {
            favouritesList.take(maxItems)
        }
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
