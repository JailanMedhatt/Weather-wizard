package com.example.weatherwizard

import android.location.Geocoder
import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import com.example.weatherwizard.data.model.FavoriteLocation
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

@Composable
fun getAddressFromLocation(latLng: Location): String {
    val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            address.getCountryName()
        } else {
            "Address Not Found !"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error Fetching Address"
    }
}