package com.kieran.winnipegbus

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import org.jetbrains.anko.db.*

class SQLiteFavouritesRepository private constructor(ctx: Context) : FavouritesRepository, ManagedSQLiteOpenHelper(ctx, "transit_db", null, 1) {
    private val favouriteRowParser = classParser<Favourite>()
    override fun get(agencyId: Long, id: Long): Favourite? {
        var favourite: Favourite? = null

        use {
            try {
                select(tableName).whereSimple("id=? and agencyId=?", id.toString(), agencyId.toString()).exec {
                    favourite = parseOpt(favouriteRowParser)
                }
            } catch (ex: SQLiteException) {
                // Well at least we tried
            }
        }

        return favourite
    }

    override fun getAll(agencyId: Long): List<Favourite>? {
        var favourites: List<Favourite>? = null

        use {
            try {
                select(tableName).whereSimple("agencyId=?", agencyId.toString()).exec {
                    favourites = parseList(favouriteRowParser)
                }
            } catch (ex: SQLiteException) {
                // Well at least we tried
            }
        }

        return favourites
    }

    override fun create(favourite: Favourite): Favourite? {
        return use {
            return@use try {
                val id = insert(tableName, "sortOrder" to favourite.sortOrder)

                get(favourite.agencyId, id)
            } catch (ex: SQLiteException) {
                null
            }
        }
    }

    override fun update(favourite: Favourite): Boolean {
        return use {
            return@use try {
                val returnCode = update(tableName, "sortOrder" to favourite.sortOrder, "homeSortOrder" to favourite.homeSortOrder, "timesUsed" to favourite.timesUsed).whereSimple("id=?", favourite.id.toString()).exec()

                returnCode > 0
            } catch (ex: SQLiteException) {
                false
            }
        }
    }

    override fun get(agencyId: Long, identifier: StopIdentifier): Favourite? {
        var favourite: Favourite? = null

        use {
            try {
                select(tableName).whereSimple("agencyId=? and agencyIdentifier=?", agencyId.toString(), identifier.toString()).exec {
                    favourite = parseOpt(favouriteRowParser)
                }
            } catch (ex: SQLiteException) {
                // Well at least we tried
            }
        }

        return favourite
    }

    override fun delete(favourite: Favourite): Boolean {
        return use {
            return@use try {
                val rowsDeleted = delete(tableName, "id=?", "id" to favourite.id)

                rowsDeleted > 0
            } catch (ex: SQLiteException) {
                false
            }
        }
    }

    init {
        instance = this
    }

    companion object {
        private const val tableName = "favourites"
        private var instance: SQLiteFavouritesRepository? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance
                ?: SQLiteFavouritesRepository(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(tableName, true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "agencyId" to INTEGER,
                "sortOrder" to INTEGER,
                "homeSortOrder" to INTEGER,
                "name" to TEXT,
                "alias" to TEXT,
                "agencyIdentifier" to TEXT,
                "timesUsed" to INTEGER,
                "latitude" to REAL,
                "longitude" to REAL,
                "agencyMetadata" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}