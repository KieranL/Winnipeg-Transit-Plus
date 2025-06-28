package com.kieran.winnipegbus.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier

@Dao
interface FavouriteDAO {
    @Query("SELECT * FROM favourites WHERE agencyId = :agencyId AND id = :id limit 1")
    fun get(agencyId: Long, id: Long): DataFavourite

    @Query("SELECT * FROM favourites WHERE agencyId = :agencyId AND agencyIdentifier = :agencyIdentifier")
    fun get(agencyId: Long, agencyIdentifier: String): List<DataFavourite>

    @Query("SELECT * FROM favourites WHERE agencyId = :agencyId")
    fun getAll(agencyId: Long): List<DataFavourite>

    @Query("DELETE FROM favourites WHERE agencyId = :agencyId and id = :id")
    suspend fun delete(agencyId: Long, id: Long)

    @Query("DELETE FROM favourites WHERE agencyId = :agencyId and agencyIdentifier = :agencyIdentifier")
    suspend fun delete(agencyId: Long, agencyIdentifier: String)
}