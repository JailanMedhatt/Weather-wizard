package com.example.weatherwizard.navigationUtills

import kotlinx.serialization.Serializable


sealed class ScreenRoute(){
    @Serializable
    object HomeRoute : ScreenRoute()
    @Serializable
    object FavRoute:ScreenRoute()
    @Serializable

    object AlertRoute:ScreenRoute()
    @Serializable

    object SettingsRoute : ScreenRoute()


}