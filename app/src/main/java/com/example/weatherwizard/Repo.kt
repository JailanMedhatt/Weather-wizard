package com.example.weatherwizard

import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.ThreeHoursResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class Repository private constructor(val remoteDataSource: RemoteDataSource){
     fun getCurrentWeather(latitude:Double,logitude:Double,lang:String,units:String): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeatherResponse(latitude=latitude,logitude=logitude,lang=lang,units=units)
    }
     fun getHoursResponse(latitude: Double,longitude: Double,lang:String,units:String): Flow<List<CurrentWeatherResponse>> {
        return remoteDataSource.getHoursResponse(latitude=latitude,logitude=longitude,lang=lang,units=units)

    }
    companion object{
        @Volatile
        private var instance : Repository?=null
        fun getInstance(remoteDataSource: RemoteDataSource): Repository {
            return instance?: synchronized(this){
                val Instance=Repository(remoteDataSource)
                instance=Instance
                Instance
            }
        }
    }
}