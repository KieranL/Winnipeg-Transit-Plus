package com.kieran.winnipegbus.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_data_cache")
data class RouteDataCache (
    @PrimaryKey
    val routeIdentifier: String,
    val textColour: String,
    val backgroundColour: String,
    val borderColour: String,
    val agencyId: Long,
) {
}