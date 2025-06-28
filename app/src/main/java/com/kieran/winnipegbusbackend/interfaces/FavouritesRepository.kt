package com.kieran.winnipegbusbackend.interfaces

import com.kieran.winnipegbus.data.DataFavourite

interface FavouritesRepository {
    fun get(agencyId: Long, id: Long): DataFavourite?

    fun getAll(agencyId: Long): List<DataFavourite>?

    suspend fun create(favourite: DataFavourite): DataFavourite?

    suspend fun update(favourite: DataFavourite): Boolean

    fun get(agencyId: Long, identifier: StopIdentifier): List<DataFavourite>?

    suspend fun delete(agencyId: Long, stopIdentifier: StopIdentifier): Boolean

    suspend fun delete(agencyId: Long, id: Long): Boolean
}