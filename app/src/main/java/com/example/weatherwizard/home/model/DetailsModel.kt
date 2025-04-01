package com.example.weatherwizard.home.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherwizard.Converters
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.data.model.FavoriteLocation
import kotlinx.serialization.Serializable

@Entity(tableName = "home_table",primaryKeys = ["latitute","longitude"])
@TypeConverters(Converters::class)
@Serializable
data class DetailsModel (val latitute: Double,val longitude: Double,
                         val currentWeatherResponse: CurrentWeatherResponse,
                         val  hoursList: List<CurrentWeatherResponse>)