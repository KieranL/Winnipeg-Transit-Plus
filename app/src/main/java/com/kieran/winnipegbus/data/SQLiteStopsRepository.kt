package com.kieran.winnipegbus.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.kieran.winnipegbusbackend.gtfs.StopsRepository
import org.jetbrains.anko.db.*

class SQLiteStopsRepository private constructor(ctx: Context) : StopsRepository, ManagedSQLiteOpenHelper(ctx, "transit_db", null, 1) {


    companion object {
        private const val tableName = "stop"
        private var instance: SQLiteStopsRepository? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance
                ?: SQLiteStopsRepository(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(tableName, true,
                "id" to TEXT,
                "agencyId" to INTEGER,
                "code" to TEXT,
                "name" to TEXT,
                "latitude" to REAL,
                "longitude" to REAL,
                "url" to TEXT,
                COMPOSITE_PRIMARY_KEY("id", "agency_id"))
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }
}