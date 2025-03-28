package com.example.weatherwizard.data.database

import com.example.weatherwizard.data.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

class LocalDataSource(val dao: LocationDao)  {
    suspend fun insertLocation(location: FavoriteLocation) {
      return  dao.insertLocation(location)
    }
    fun getLocations(): Flow<List<FavoriteLocation>> {
        return dao.getLocations()
    }
    suspend fun deleteLocation(location: FavoriteLocation) {
        return dao.deleteLocation(location)
}}