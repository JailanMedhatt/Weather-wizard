package com.example.weatherwizard.Pojos

import kotlinx.serialization.Serializable

@Serializable
data class Sys(
    val country: String?,
    val sunrise: Int?,
    val sunset: Int?
)