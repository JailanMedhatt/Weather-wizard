package com.example.weatherwizard.navigationUtills

import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.data.model.FavoriteLocation
import com.example.weatherwizard.home.viewModel.HomeViewModel
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(){
    @Serializable
    data class HomeRoute( val longitude: Double?, val latitude: Double?,val weatherObj :String) : ScreenRoute()
    @Serializable
    object FavRoute:ScreenRoute()
    @Serializable

    object AlertRoute:ScreenRoute()
    @Serializable

    object SettingsRoute : ScreenRoute()
    @Serializable
    data class MapRoute (val fromSettings:Boolean): ScreenRoute()
    @Serializable
    object SplashRoute:ScreenRoute()

}