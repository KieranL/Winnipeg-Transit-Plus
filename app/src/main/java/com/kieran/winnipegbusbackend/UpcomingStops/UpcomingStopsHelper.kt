package com.kieran.winnipegbusbackend.UpcomingStops

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UpcomingStopsHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        val TABLE_STOP_LOG = "UpcomingStops"
        val COLUMN_ID = "UpcomingStopId"
        val COLUMN_ROUTE_KEY = "RouteKey"
        val COLUMN_STOP_NUMBER = "StopNumber"
        val COLUMN_STOP_ON_ROUTE = "StopNumber"
        val COLUMN_LOG_TIME = "log_time"

        val DATABASE_NAME = TABLE_STOP_LOG + ".db"
        private val DATABASE_VERSION = 1

        private val CREATE_DATABASE = ("create table " + TABLE_STOP_LOG + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_ROUTE_KEY + " text not null, "
                + COLUMN_STOP_NUMBER + " integer not null, "
                + COLUMN_STOP_ON_ROUTE + " integer not null, "
                + COLUMN_LOG_TIME + " integer not null);")
    }
}