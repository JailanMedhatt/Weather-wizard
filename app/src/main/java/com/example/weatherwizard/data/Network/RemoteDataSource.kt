package com.example.weatherwizard.Network

import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.ThreeHoursResponse
import retrofit2.Response


class RemoteDataSource (private val service  : RetrofitHandler){
    suspend fun getCurrentWeatherResponse(latitude:Double,logitude:Double):Response<CurrentWeatherResponse>{
        return service.getCurrentWeatherResponse(latitude = latitude,longitude = logitude)
    }
    suspend fun getHoursResponse(latitude: Double,logitude: Double):Response<ThreeHoursResponse>{
        return service.getHoursResponse(latitude = latitude,longitude = logitude)

    }
}