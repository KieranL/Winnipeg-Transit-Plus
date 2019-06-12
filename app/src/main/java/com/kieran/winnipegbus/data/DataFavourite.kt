package com.kieran.winnipegbus.data

data class DataFavourite (
        val id: Long,
        val agencyId: Long,
        val sortOrder: Int?,
        val homeSortOrder: Int?,
        val name: String,
        val alias: String?,
        val agencyIdentifier: String,
        val timesUsed: Int,
        val latitude: Double?,
        val longitude: Double?,
        val agencyMetadata: String?
)