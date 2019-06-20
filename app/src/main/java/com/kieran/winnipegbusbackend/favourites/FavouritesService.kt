package com.kieran.winnipegbusbackend.favourites

import com.kieran.winnipegbus.data.DataFavourite
import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouritesImporter
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.GeoLocation
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

class FavouritesService(private val favouritesRepository: FavouritesRepository, private val agencyId: Long) {
    companion object {
        private var instance: FavouritesService? = null

        @Synchronized
        fun getInstance(favouritesRepository: FavouritesRepository, agencyId: Long) = instance
                ?: FavouritesService(favouritesRepository, agencyId)
    }

    fun getAll(sortPreference: FavouritesListSortType = FavouritesListSortType.getEnum("0")): List<FavouriteStop> {
        if(!favouritesRepository.hasBeenImported()) {
            runImport()
        }

        val dataFavourites = favouritesRepository.getAll(agencyId) ?: throw Exception()

        return sort(convertFromDataClass( dataFavourites ), sortPreference)
    }

    private fun runImport() {
        FavouritesImporter.convertXMLtoSQLite(this)

        favouritesRepository.markImported()
    }

    fun contains(identifier: StopIdentifier): Boolean {
        return favouritesRepository.get(agencyId, identifier) != null
    }

    fun add(favourite: FavouriteStop): FavouriteStop? {
        val new = favouritesRepository.create(convertToDataClass(favourite, agencyId)) ?: throw Exception()

        return convertFromDataClass(new)
    }

    fun update(favourite: FavouriteStop): Boolean {
        return favouritesRepository.update(convertToDataClass(favourite, agencyId))
    }

    fun delete(stopIdentifier: StopIdentifier): Boolean {
        return favouritesRepository.delete(agencyId, stopIdentifier)
    }

    fun convertFromDataClass(favourite: DataFavourite): FavouriteStop? {
        val identifier = AgencySpecificClassFactory.createStopIdentifier(favourite.agencyId, favourite.agencyIdentifier)
        val latlng = if (favourite.latitude != null && favourite.longitude != null) GeoLocation(favourite.latitude, favourite.longitude) else null

        return if (identifier != null) FavouriteStop(favourite.name, identifier, favourite.timesUsed, latlng, favourite.id, favourite.alias) else null
    }

    fun convertFromDataClass(favourites: List<DataFavourite>): List<FavouriteStop> {
        val convertedFavourites = arrayListOf<FavouriteStop>()

        for (favourite in favourites) {
            val converted = this.convertFromDataClass(favourite)

            if (converted != null) {
                convertedFavourites.add(converted)
            }
        }

        return convertedFavourites
    }

    fun convertToDataClass(favourite: FavouriteStop, agencyId: Long): DataFavourite {
        return DataFavourite(favourite.id, agencyId, null, null, favourite.name, favourite.alias, favourite.identifier.toString(), favourite.timesUsed, favourite.latLng?.latitude, favourite.latLng?.longitude, null)
    }

    fun sort(favouritesList: List<FavouriteStop>, sortType: FavouritesListSortType): List<FavouriteStop> {
        return favouritesList.sortedWith(Comparator{ stop1, stop2 ->
            when (sortType) {
                FavouritesListSortType.STOP_NUMBER_ASC -> stop1.identifier.compareTo(stop2.identifier)
                FavouritesListSortType.STOP_NUMBER_DESC -> -stop1.identifier.compareTo(stop2.identifier)
                FavouritesListSortType.FREQUENCY_ASC -> stop1.timesUsed - stop2.timesUsed
                FavouritesListSortType.FREQUENCY_DESC -> -(stop1.timesUsed - stop2.timesUsed)
            }
        })
    }
}