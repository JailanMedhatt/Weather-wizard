package com.example.weatherwizard.Network


import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.ThreeHoursResponse
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query


val apiKey ="f43cdb818e5d692f028dfc4bf07e12be"

interface RetrofitHandler{
    @GET("weather")
   suspend fun getCurrentWeatherResponse(@Query("lat" )  latitude:Double,@Query("lon" )  longitude:Double,@Query("lang") lang:String,@Query("units") units:String,@Query("appid" )  appid:String= apiKey): Response<CurrentWeatherResponse>
    @GET("forecast")
    suspend fun getHoursResponse(@Query("lat" )  latitude:Double,@Query("lon" )  longitude:Double,@Query("lang") lang:String,@Query("units") units:String,@Query("appid" )  appid:String= apiKey): Response<ThreeHoursResponse>
}
object RetrofitHelper{
    val baseUrl ="https://api.openweathermap.org/data/2.5/"

    val retrofitInstance= Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
        .build().create(RetrofitHandler::class.java)
}