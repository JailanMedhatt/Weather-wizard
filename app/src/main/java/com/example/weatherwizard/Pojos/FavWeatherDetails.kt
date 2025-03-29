package com.example.weatherwizard.Pojos

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherwizard.Converters
import com.example.weatherwizard.data.model.FavoriteLocation
import kotlinx.serialization.Serializable

@Entity(tableName = "location_table")
@TypeConverters(Converters::class)
@Serializable
data class FavWeatherDetails (val favoriteLocation: FavoriteLocation,
                              val currentWeatherResponse: CurrentWeatherResponse,
                              val  hoursList: List<CurrentWeatherResponse>,@PrimaryKey(autoGenerate = true) val id: Int = 0,)