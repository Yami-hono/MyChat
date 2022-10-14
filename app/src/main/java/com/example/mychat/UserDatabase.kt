package com.example.mychat

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class PostDatabase : RoomDatabase(){

    abstract fun contactDAO():PostDao
}