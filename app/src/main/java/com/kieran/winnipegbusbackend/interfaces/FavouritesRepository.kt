package com.kieran.winnipegbusbackend.interfaces

import com.kieran.winnipegbus.Favourite

interface FavouritesRepository {
    fun get(agencyId: Long, id: Long): Favourite?

    fun getAll(agencyId: Long): List<Favourite>?

    fun create(favourite: Favourite): Favourite?

    fun update(favourite: Favourite): Boolean

    fun get(agencyId: Long, identifier: StopIdentifier): Favourite?

    fun delete(favourite: Favourite): Boolean
}