package com.example.weatherwizard.data.database

import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.home.model.DetailsModel
import kotlinx.coroutines.flow.Flow

class LocalDataSource(val dao: LocationDao, val alertDao: AlertDao, val homeDao: HomeDao)  {
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
    suspend fun insertDetails(detailsModel: DetailsModel) {
        return homeDao.insertDetails(detailsModel)
    }
    fun getDetails(): Flow<List<DetailsModel>> {
        return homeDao.getDetails()}
    suspend fun deleteDetails(detailsModel: DetailsModel) {
        return homeDao.deleteDetails(detailsModel)
    }



}