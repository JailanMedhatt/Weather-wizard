package com.example.weatherwizard.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.home.model.DetailsModel

@Database(entities = [ FavWeatherDetails::class, AlertModel::class, DetailsModel::class] , version = 4)
abstract class AppDb :RoomDatabase() {
    abstract fun getDao(): LocationDao
    abstract fun getAlertDao(): AlertDao
    abstract fun getHomeDao(): HomeDao
    companion object{
        @Volatile
        private var instance :AppDb?=null
        fun getInstance(context: Context):AppDb{
            return instance?: synchronized(this){
                val Instance= Room.databaseBuilder(context.applicationContext,AppDb::class.java,"products").fallbackToDestructiveMigration().build()
                instance=Instance
                Instance
            }
        }
    }
}