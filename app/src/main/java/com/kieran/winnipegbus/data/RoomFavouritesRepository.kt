package com.kieran.winnipegbus.data

import android.content.Context
import androidx.room.Room
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class RoomFavouritesRepository private constructor(ctx: Context) : FavouritesRepository {
    private val db = Room.databaseBuilder(
        ctx,
        FavouriteDB::class.java, "transit_db"
    ).build()

    companion object {
        private var instance: RoomFavouritesRepository? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance
            ?: RoomFavouritesRepository(ctx.applicationContext)
    }

    override fun get(agencyId: Long, id: Long): DataFavourite {
        return db.favouritesDao().get(agencyId, id)
    }

    override fun get(agencyId: Long, identifier: StopIdentifier): List<DataFavourite> {
       return db.favouritesDao().get(agencyId, identifier.toString())
    }

    override fun getAll(agencyId: Long): List<DataFavourite> {
        return db.favouritesDao().getAll(agencyId)
    }

    override suspend fun create(favourite: DataFavourite): DataFavourite? {
        val id = db.favouritesDao().create(favourite)

        val newFavourite = get(favourite.agencyId!!, id)

        return newFavourite
    }

    override suspend fun update(favourite: DataFavourite): Boolean {
        db.favouritesDao().update(favourite)

        return true
    }

    override suspend fun delete(agencyId: Long, stopIdentifier: StopIdentifier): Boolean {
        db.favouritesDao().delete(agencyId, stopIdentifier.toString())

        return true
    }

    override suspend fun delete(agencyId: Long, id: Long): Boolean {
        db.favouritesDao().delete(agencyId, id)

        return true
    }
}