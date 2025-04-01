package com.example.weatherwizard.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.home.model.DetailsModel
import kotlinx.coroutines.flow.Flow
@Dao
interface LocationDao {
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertLocation(location: FavoriteLocation)
//    @Query("SELECT * FROM location_table")
//     fun getLocations(): Flow<List<FavoriteLocation>>
//    @Delete
//    suspend fun deleteLocation(product: FavoriteLocation)
    @Insert
    suspend fun insertFavLocationDetails(favWeatherDetails: FavWeatherDetails)
    @Query("SELECT * FROM location_table")
    fun getFavLocations(): Flow<List<FavWeatherDetails>>
    @Delete
    suspend fun deleteLocation(favWeatherDetails: FavWeatherDetails)



}
@Dao
interface AlertDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlert(alert: AlertModel)
    @Query("SELECT * FROM alerts")
    fun getAlerts(): Flow<List<AlertModel>>
    @Delete
    suspend fun deleteAlert(alert: AlertModel)


}
@Dao
interface HomeDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(detailsModel: DetailsModel)
    @Query("SELECT * FROM home_table")
    fun getDetails(): Flow<List<DetailsModel>>
    @Delete
    suspend fun deleteDetails(detailsModel: DetailsModel)


}
