package com.kieran.winnipegbus.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RouteDataCache::class], version = 1)
abstract class RouteCacheDB : RoomDatabase() {
    abstract fun routeDataCacheDAO(): RouteDataCacheDAO
}