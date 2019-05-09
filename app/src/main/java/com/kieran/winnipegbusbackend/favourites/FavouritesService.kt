package com.kieran.winnipegbusbackend.favourites

import com.kieran.winnipegbus.Favourite
import com.kieran.winnipegbusbackend.AgencySpecificClassFactory
import com.kieran.winnipegbusbackend.common.FavouriteStop
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

class FavouritesService(private val favouritesRepository: FavouritesRepository, private val agencyId: Long) {
    companion object {
        private var instance: FavouritesService? = null

        @Synchronized
        fun getInstance(favouritesRepository: FavouritesRepository) = instance ?: FavouritesService(favouritesRepository, 1)
    }

    fun getAll(): List<Favourite> {
        return favouritesRepository.getAll(agencyId) ?: throw Exception()
    }

    fun contains(identifier: StopIdentifier): Boolean{
        return favouritesRepository.get(agencyId, identifier) != null
    }

    fun add(favourite: Favourite): Favourite {
        return favouritesRepository.create(favourite) ?: throw Exception()
    }

    fun update(favourite: Favourite): Boolean {
        return favouritesRepository.update(favourite)
    }

    fun delete(favourite: Favourite): Boolean {
        return favouritesRepository.delete(favourite)
    }

    fun convert(favourite: Favourite): FavouriteStop? {
        val identifier = AgencySpecificClassFactory.createStopIdentifier(favourite.agencyId, favourite.agencyIdentifier)
        val 
        return if (identifier != null) FavouriteStop(favourite.name, identifier, favourite.timesUsed) else null
    }

    fun convert(favourites: List<Favourite>): ArrayList<FavouriteStop> {
        val convertedFavourites = ArrayList<FavouriteStop>()

        for(favourite in favourites) {
            val converted = this.convert(favourite)

            if(converted != null) {
                convertedFavourites.add(converted)
            }
        }

        return convertedFavourites
    }
}