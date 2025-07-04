package com.kieran.winnipegbus.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataFavourite::class], version = 2)
abstract class FavouriteDB : RoomDatabase() {
    abstract fun favouritesDao(): FavouriteDAO
}