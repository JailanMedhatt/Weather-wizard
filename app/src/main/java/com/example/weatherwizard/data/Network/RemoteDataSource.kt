package com.example.weatherwizard.Network

import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.ThreeHoursResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response


class RemoteDataSource (private val service  : RetrofitHandler){
     fun getCurrentWeatherResponse(latitude:Double,logitude:Double):Flow<CurrentWeatherResponse> = flow {
       val res= service.getCurrentWeatherResponse(latitude = latitude,longitude = logitude)
        if(res.isSuccessful){
            emit(res.body()!!)
        }
    }


     fun getHoursResponse(latitude: Double,logitude: Double) :Flow<List<CurrentWeatherResponse>> = flow {
     val res=   service.getHoursResponse(latitude = latitude,longitude = logitude)
        if(res.isSuccessful){
            emit(res.body()!!.list)

        }
    }




}