package com.example.weatherwizard

import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.data.database.LocalDataSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(val remoteDataSource: RemoteDataSource,val localDataSource: LocalDataSource){
     fun getCurrentWeather(latitude:Double,logitude:Double,lang:String,units:String): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeatherResponse(latitude=latitude,logitude=logitude,lang=lang,units=units)
    }
     fun getHoursResponse(latitude: Double,longitude: Double,lang:String,units:String): Flow<List<CurrentWeatherResponse>> {
        return remoteDataSource.getHoursResponse(latitude=latitude,logitude=longitude,lang=lang,units=units)

    }
    suspend fun insertFavouriteLocation(location: FavWeatherDetails) {
        localDataSource.insertLocation(location)
    }
    fun getLocations(): Flow<List<FavWeatherDetails>> {
        return localDataSource.getLocations()}
    suspend fun deleteLocation(location: FavWeatherDetails) {
        localDataSource.deleteLocation(location)
    }
    suspend fun insertAlert(alert: AlertModel) {
        localDataSource.insertAlert(alert)
    }
    fun getAlerts(): Flow<List<AlertModel>> {
        return localDataSource.getAlerts()}
    suspend fun deleteAlert(alert: AlertModel) {
        localDataSource.deleteAlert(alert)
    }



    companion object{
        @Volatile
        private var instance : Repository?=null
        fun getInstance(remoteDataSource: RemoteDataSource,localDataSource: LocalDataSource): Repository {
            return instance?: synchronized(this){
                val Instance=Repository(remoteDataSource,localDataSource)
                instance=Instance
                Instance
            }
        }
    }
}