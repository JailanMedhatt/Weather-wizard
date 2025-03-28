package com.example.weatherwizard.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherwizard.data.model.FavoriteLocation

@Database(entities = [ FavoriteLocation::class] , version = 1)
abstract class AppDb :RoomDatabase() {
    abstract fun getDao(): LocationDao
    companion object{
        @Volatile
        private var instance :AppDb?=null
        fun getInstance(context: Context):AppDb{
            return instance?: synchronized(this){
                val Instance= Room.databaseBuilder(context.applicationContext,AppDb::class.java,"products").build()
                instance=Instance
                Instance
            }
        }
    }
}