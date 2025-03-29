package com.example.weatherwizard.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwizard.Repository
import com.example.weatherwizard.alert.model.AlertModel
import com.example.weatherwizard.fav.viewModel.FavouriteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(val repository: Repository): ViewModel() {
    private var mutableAlerts = MutableStateFlow<List<AlertModel>>(emptyList())
    val alerts = mutableAlerts.asStateFlow()

    class AlertViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlertViewModel(repository) as T
        }
    }
    fun insertAlert(alarm: AlertModel) {
        viewModelScope.launch {
            repository.insertAlert(alarm)
        }


    }
    fun getAlerts(){
        viewModelScope.launch {
            repository.getAlerts().collect{
                mutableAlerts.value=it
            }
        }
    }
}