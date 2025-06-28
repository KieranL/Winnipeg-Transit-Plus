package com.kieran.winnipegbus.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites")
data class DataFavourite (
        @PrimaryKey(autoGenerate = true) val id: Long?,
        @ColumnInfo(name = "agencyId") val agencyId: Long?,
        @ColumnInfo(name = "sortOrder") val sortOrder: Int?,
        @ColumnInfo(name = "homeSortOrder") val homeSortOrder: Int?,
        @ColumnInfo(name = "name") val name: String?,
        @ColumnInfo(name = "alias") val alias: String?,
        @ColumnInfo(name = "agencyIdentifier") val agencyIdentifier: String?,
        @ColumnInfo(name = "timesUsed") val timesUsed: Int?,
        @ColumnInfo(name = "latitude") val latitude: Double?,
        @ColumnInfo(name = "longitude") val longitude: Double?,
        @ColumnInfo(name = "agencyMetadata") val agencyMetadata: String?,
        @ColumnInfo(name = "routes") val routes: String?
)