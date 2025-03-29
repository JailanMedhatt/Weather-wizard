package com.example.weatherwizard.data.model

import androidx.room.Entity
import kotlinx.serialization.Serializable

//@Entity(tableName = "location_table",primaryKeys = ["longitude","latitude"])
@Serializable
data class FavoriteLocation(val longitude: Double, val latitude: Double, val address: String)
