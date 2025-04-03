package com.example.weatherwizard.alert.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwizard.data.Repository
import com.example.weatherwizard.alert.model.AlertModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AlertViewModel(val repository: Repository): ViewModel() {
    private var mutableAlerts = MutableStateFlow<List<AlertModel>>(emptyList())
    val alerts = mutableAlerts.asStateFlow()
    private val mutableMessage = MutableStateFlow<String?>(null) // Holds last message
    val message = mutableMessage.asStateFlow()

    class AlertViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlertViewModel(repository) as T
        }
    }
    fun insertAlert(alarm: AlertModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAlert(alarm)
            mutableMessage.value=("Alarm added successfully")

        }


    }
    fun getAlerts(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAlerts().collect{
                it.forEachIndexed { index, alertModel ->
                if(calculateDelay(alertModel.date,alertModel.time)<=0){
                    repository.deleteAlert(alertModel)
                    mutableMessage.value=("Alarm deleted successfully")

                } }
                mutableAlerts.value=it
            }
        }
    }
    fun calculateDelay(dateStr: String, timeStr: String): Long {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) // Match your input format
        return try {
            val selectedDateTime = sdf.parse("$dateStr $timeStr")?.time ?: return 0
            val currentTime = System.currentTimeMillis()
            val delay = selectedDateTime - currentTime
            if (delay > 0) delay else 0  // If delay is negative, return 0 to avoid immediate execution
        } catch (e: Exception) {
            e.printStackTrace()
            0 // Return 0 if parsing fails
        }
    }
    fun  deleteAlert(alarm: AlertModel){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(alarm)
            mutableMessage.emit("Alarm deleted successfully")
        viewModelScope.launch {
            repository.deleteAlert(alarm)
        }

    }
}}