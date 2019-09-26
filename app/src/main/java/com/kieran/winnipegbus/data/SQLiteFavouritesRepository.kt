package com.kieran.winnipegbus.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import com.kieran.winnipegbusbackend.interfaces.FavouritesRepository
import com.kieran.winnipegbusbackend.interfaces.StopIdentifier
import com.rollbar.android.Rollbar
import org.jetbrains.anko.db.*

class SQLiteFavouritesRepository private constructor(ctx: Context) : FavouritesRepository, ManagedSQLiteOpenHelper(ctx, "transit_db", null, 2) {
    private val favouriteRowParser = classParser<DataFavourite>()
    private var isImported: Boolean = false
    private val INITIALIZED_STOP_IDENTIFIER: StopIdentifier = InitializedIdentifier("init")

    override fun getAll(agencyId: Long): List<DataFavourite>? {
        var favourites: List<DataFavourite>? = null

        use {
            try {
                select(tableName).whereSimple("agencyId=?", agencyId.toString()).exec {
                    favourites = parseList(favouriteRowParser)
                }
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
            }
        }

        return favourites
    }

    override fun create(favourite: DataFavourite): DataFavourite? {
        return use {
            return@use try {
                val id = insert(
                        tableName,
                        "agencyId" to favourite.agencyId,
                        "sortOrder" to favourite.sortOrder,
                        "homeSortOrder" to favourite.homeSortOrder,
                        "name" to favourite.name,
                        "alias" to favourite.alias,
                        "agencyIdentifier" to favourite.agencyIdentifier,
                        "timesUsed" to favourite.timesUsed,
                        "latitude" to favourite.latitude,
                        "longitude" to favourite.longitude,
                        "agencyMetadata" to favourite.agencyMetadata,
                        "routes" to favourite.routes)
                get(favourite.agencyId, id)
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
                null
            }
        }
    }

    override fun update(favourite: DataFavourite): Boolean {
        return use {
            return@use try {
                val returnCode = update(
                        tableName,
                        "agencyId" to favourite.agencyId,
                        "sortOrder" to favourite.sortOrder,
                        "homeSortOrder" to favourite.homeSortOrder,
                        "name" to favourite.name,
                        "alias" to favourite.alias,
                        "agencyIdentifier" to favourite.agencyIdentifier,
                        "timesUsed" to favourite.timesUsed,
                        "latitude" to favourite.latitude,
                        "longitude" to favourite.longitude,
                        "agencyMetadata" to favourite.agencyMetadata,
                        "routes" to favourite.routes
                ).whereSimple("id=?", favourite.id.toString()).exec()

                returnCode > 0
            } catch (ex: SQLiteException) {
                false
            }
        }
    }

    override fun get(agencyId: Long, identifier: StopIdentifier): List<DataFavourite>? {
        var favourites: List<DataFavourite>? = null

        use {
            try {
                select(tableName).whereSimple("agencyId=? and agencyIdentifier=?", agencyId.toString(), identifier.toString()).exec {
                    if(count > 0) {
                        favourites = parseList(favouriteRowParser)
                    }
                }
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
            }
        }

        return favourites
    }

    override fun delete(agencyId: Long, stopIdentifier: StopIdentifier): Boolean {
        return use {
            return@use try {
                val rowsDeleted = delete(tableName, "agencyId={agencyId} and agencyIdentifier={agencyIdentifier}", "agencyId" to agencyId, "agencyIdentifier" to stopIdentifier.toString())

                rowsDeleted > 0
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
                false
            }
        }
    }

    override fun get(agencyId: Long, id: Long): DataFavourite? {
        var favourite: DataFavourite? = null

        use {
            try {
                select(tableName).whereSimple("id=? and agencyId=?", id.toString(), agencyId.toString()).exec {
                    favourite = parseOpt(favouriteRowParser)
                }
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
            }
        }

        return favourite
    }

    override fun delete(agencyId: Long, id: Long): Boolean {
        return use {
            return@use try {
                val rowsDeleted = delete(tableName, "agencyId={agencyId} and id={id}", "agencyId" to agencyId, "id" to id)

                rowsDeleted > 0
            } catch (ex: SQLiteException) {
                Rollbar.instance()?.error(ex)
                false
            }
        }
    }

    override fun hasBeenImported(): Boolean {
        if(!isImported) {
            val importedIndicator = get(1, INITIALIZED_STOP_IDENTIFIER)

            isImported = importedIndicator != null
        }

        return isImported
    }

    override fun markImported() {
        create(DataFavourite(-1, 1, null, null, "", null, INITIALIZED_STOP_IDENTIFIER.toString(), 0, null, null, null, null))
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

        private data class InitializedIdentifier(val identifier:String): StopIdentifier {
            override fun compareTo(other: StopIdentifier): Int {
                return 0
            }

            override fun toString(): String {
                return identifier
            }
        }
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
                "agencyMetadata" to TEXT,
                "routes" to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE favourites ADD COLUMN routes TEXT;")
        }
    }
}