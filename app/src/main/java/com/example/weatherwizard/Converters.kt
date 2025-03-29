package com.example.weatherwizard

import androidx.room.TypeConverter
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.data.model.FavoriteLocation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromFavoriteLocation(value: FavoriteLocation): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toFavoriteLocation(value: String): FavoriteLocation {
        return gson.fromJson(value, FavoriteLocation::class.java)
    }

    @TypeConverter
    fun fromCurrentWeatherResponse(value: CurrentWeatherResponse): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCurrentWeatherResponse(value: String): CurrentWeatherResponse {
        return gson.fromJson(value, CurrentWeatherResponse::class.java)
    }

    @TypeConverter
    fun fromCurrentWeatherResponseList(value: List<CurrentWeatherResponse>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCurrentWeatherResponseList(value: String): List<CurrentWeatherResponse> {
        val listType = object : TypeToken<List<CurrentWeatherResponse>>() {}.type
        return gson.fromJson(value, listType)
    }
}

