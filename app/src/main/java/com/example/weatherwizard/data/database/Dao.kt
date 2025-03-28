package com.example.weatherwizard.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.weatherwizard.data.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow
@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLocation(location: FavoriteLocation)
    @Query("SELECT * FROM location_table")
     fun getLocations(): Flow<List<FavoriteLocation>>
    @Delete
    suspend fun deleteLocation(product: FavoriteLocation)

}