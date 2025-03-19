package com.example.weatherwizard.Pojos

data class CurrentWeatherResponse(
    var base: String?,
    var clouds: Clouds?,
    var cod: Int?,
    var coord: Coord?,
    var dt: Int?,
    var id: Int?,
    val main: Main?,
    val name: String?,
    val sys: Sys?,
    val timezone: Int?,
    val visibility: Int?,
    val weather: List<Weather>?,
    val wind: Wind?,
    val dt_txt:String?
)