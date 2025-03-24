package com.example.weatherwizard.Network

import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow



class RemoteDataSource (private val service  : RetrofitHandler){
     fun getCurrentWeatherResponse(latitude:Double,logitude:Double,lang:String, units:String):Flow<CurrentWeatherResponse> = flow {
       val res= service.getCurrentWeatherResponse(latitude = latitude,longitude = logitude, units =units,lang=lang)
        if(res.isSuccessful){
            emit(res.body()!!)
        }
    }

     fun getHoursResponse(latitude: Double,logitude: Double,lang:String, units:String) :Flow<List<CurrentWeatherResponse>> = flow {
     val res=   service.getHoursResponse(latitude = latitude,longitude = logitude, units =units,lang=lang)
        if(res.isSuccessful){
            emit(res.body()!!.list)

        }
    }




}