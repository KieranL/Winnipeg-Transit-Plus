package com.kieran.winnipegbus.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FavouriteDAO {
    @Insert
    suspend fun create(favourite: DataFavourite): Long

    @Update
    suspend fun update(favourite: DataFavourite)

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