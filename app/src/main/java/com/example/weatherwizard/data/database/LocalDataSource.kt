package com.example.weatherwizard.data.database

import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.alert.model.AlertModel
import kotlinx.coroutines.flow.Flow

class LocalDataSource(val dao: LocationDao, val alertDao: AlertDao)  {
//    suspend fun insertLocation(location: FavoriteLocation) {
//      return  dao.insertLocation(location)
//    }
//    fun getLocations(): Flow<List<FavoriteLocation>> {
//        return dao.getLocations()
//    }
//    suspend fun deleteLocation(location: FavoriteLocation) {
//        return dao.deleteLocation(location)
//}
    suspend fun insertLocation(location: FavWeatherDetails) {
        return dao.insertFavLocationDetails(location)
    }
    fun getLocations(): Flow<List<FavWeatherDetails>> {
        return dao.getFavLocations()
}
    suspend fun deleteLocation(location: FavWeatherDetails) {
        return dao.deleteLocation(location)

    }
    suspend fun insertAlert(alert: AlertModel) {
        return alertDao.insertAlert(alert)
    }
    fun getAlerts(): Flow<List<AlertModel>> {
        return alertDao.getAlerts()

    }
    suspend fun deleteAlert(alert: AlertModel) {
        return alertDao.deleteAlert(alert)
    }

}