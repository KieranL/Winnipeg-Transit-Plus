package com.kieran.winnipegbusbackend.UpcomingStops;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UpcomingStopsHelper extends SQLiteOpenHelper {
    public static final String TABLE_STOP_LOG = "UpcomingStops";
    public static final String COLUMN_ID = "UpcomingStopId";
    public static final String COLUMN_ROUTE_KEY = "RouteKey";
    public static final String COLUMN_STOP_NUMBER = "StopNumber";
    public static final String COLUMN_STOP_ON_ROUTE = "StopNumber";
    public static final String COLUMN_LOG_TIME = "log_time";

    public static final String DATABASE_NAME = TABLE_STOP_LOG + ".db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_DATABASE = "create table " + TABLE_STOP_LOG + "("
            + COLUMN_ID         + " integer primary key autoincrement, "
            + COLUMN_ROUTE_KEY + " text not null, "
            + COLUMN_STOP_NUMBER     + " integer not null, "
            + COLUMN_STOP_ON_ROUTE     + " integer not null, "
            + COLUMN_LOG_TIME   + " integer not null);";

    public UpcomingStopsHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}