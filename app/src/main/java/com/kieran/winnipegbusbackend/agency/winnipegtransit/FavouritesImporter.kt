package com.kieran.winnipegbusbackend.agency.winnipegtransit

import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.favourites.FavouritesService

object FavouritesImporter {
    fun convertXMLtoSQLite(favouritesService: FavouritesService) {
        val oldFavourites = FavouriteStopsList.getFavouriteStopsSorted(FavouritesListSortType.STOP_NUMBER_ASC)

        for (favourite in oldFavourites) {
            favouritesService.add(favourite)
        }
    }
}