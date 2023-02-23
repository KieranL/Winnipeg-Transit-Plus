package com.kieran.winnipegbusbackend.favourites

import com.kieran.winnipegbus.data.DataFavourite
import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.agency.winnipegtransit.FavouritesImporter
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.common.GeoLocation
import com.kieran.winnipegbusbackend.enums.FavouritesListSortType
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.RouteIdentifier
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

class FavouritesService(private val favouritesRepository: FavouritesRepository, private val agencyId: Long) {
    companion object {
        private var instance: FavouritesService? = null

        @Synchronized
        fun getInstance(favouritesRepository: FavouritesRepository, agencyId: Long) = instance
                ?: FavouritesService(favouritesRepository, agencyId)
    }

    fun getAll(sortPreference: FavouritesListSortType = FavouritesListSortType.getEnum("0")): List<FavouriteStop> {
        if (!favouritesRepository.hasBeenImported()) {
            runImport()
        }

        val dataFavourites = favouritesRepository.getAll(agencyId) ?: throw Exception()

        return sort(convertFromDataClass(dataFavourites), sortPreference)
    }

    private fun runImport() {
        FavouritesImporter.convertXMLtoSQLite(this)

        favouritesRepository.markImported()
    }

    fun contains(identifier: StopIdentifier): Boolean {
        return favouritesRepository.get(agencyId, identifier) != null
    }

    fun get(identifier: StopIdentifier): FavouriteStop? {
        val matchingFavourite = favouritesRepository.get(agencyId, identifier)?.first()

        return if (matchingFavourite == null)
            null
        else
            convertFromDataClass(matchingFavourite)
    }

    fun add(favourite: FavouriteStop): FavouriteStop? {
        val existing = favouritesRepository.get(agencyId, favourite.identifier)

        if (existing != null) {
            for (existingDataFavourite in existing) {
                val existingFavourite = convertFromDataClass(existingDataFavourite)

                if (existingFavourite?.routes != null && favourite.routes != null) {
                    if (existingFavourite.routes!!.count() == favourite.routes!!.count() && existingFavourite.routes!!.containsAll(favourite.routes!!))
                        return existingFavourite
                }
            }
        }

        val new = favouritesRepository.create(convertToDataClass(favourite, agencyId))
                ?: throw Exception()

        return convertFromDataClass(new)
    }

    fun update(favourite: FavouriteStop): Boolean {
        return favouritesRepository.update(convertToDataClass(favourite, agencyId))
    }

    fun delete(id: Long): Boolean {
        return favouritesRepository.delete(agencyId, id)
    }

    fun convertFromDataClass(favourite: DataFavourite): FavouriteStop? {
        val identifier = AgencySpecificClassFactory.createStopIdentifier(favourite.agencyId, favourite.agencyIdentifier)
        val latlng = if (favourite.latitude != null && favourite.longitude != null) GeoLocation(favourite.latitude, favourite.longitude) else null
        var routes: ArrayList<RouteIdentifier>? = null

        if (favourite.routes != null && favourite.routes.isNotEmpty()) {
            routes = ArrayList()

            for(route in favourite.routes.split(";")) {
                AgencySpecificClassFactory.createRouteIdentifier(agencyId, route)?.let { routes.add(it) }
            }
        }

        return if (identifier != null) FavouriteStop(favourite.name, identifier, favourite.timesUsed, latlng, favourite.id, favourite.alias, routes) else null
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
        var routes: String? = null

        if (favourite.routes != null && favourite.routes!!.any()) {
            routes = favourite.routes?.sorted()?.joinToString(";") {
                it.toDataString()
            }
        }

        return DataFavourite(favourite.id, agencyId, null, null, favourite.name, favourite.alias, favourite.identifier.toString(), favourite.timesUsed, favourite.latLng?.latitude, favourite.latLng?.longitude, null, routes)
    }

    fun sort(favouritesList: List<FavouriteStop>, sortType: FavouritesListSortType): List<FavouriteStop> {
        return favouritesList.sortedWith(Comparator { stop1, stop2 ->
            when (sortType) {
                FavouritesListSortType.STOP_NUMBER_ASC -> stop1.identifier.compareTo(stop2.identifier)
                FavouritesListSortType.STOP_NUMBER_DESC -> -stop1.identifier.compareTo(stop2.identifier)
                FavouritesListSortType.FREQUENCY_ASC -> stop1.timesUsed - stop2.timesUsed
                FavouritesListSortType.FREQUENCY_DESC -> -(stop1.timesUsed - stop2.timesUsed)
            }
        })
    }
}