package com.kieran.winnipegbus.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface RouteDataCacheDAO {
    @Upsert
    fun upsert(data: RouteDataCache)

    @Query("SELECT * FROM route_data_cache WHERE agencyId = :agencyId AND routeIdentifier = :id limit 1")
    fun get(id: String, agencyId: Long): RouteDataCache?

    @Query("SELECT * FROM route_data_cache WHERE agencyId = :agencyId")
    fun all(agencyId: Long): List<RouteDataCache>
}