package com.example.weatherwizard

import com.example.weatherwizard.Pojos.CurrentWeatherResponse

sealed class Response {
    data class CurrentWeatherSuccess(val data: CurrentWeatherResponse) : Response()
    data class HoursOrDaysSuccess(val data: List<CurrentWeatherResponse>) : Response()
    data class Error(val message: String) : Response()
    object Loading : Response()

}