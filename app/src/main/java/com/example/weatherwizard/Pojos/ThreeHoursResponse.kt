package com.example.weatherwizard.Pojos

import kotlinx.serialization.Serializable

@Serializable
data class ThreeHoursResponse(val list:List<CurrentWeatherResponse>,val city :City)
