package com.example.weatherwizard.fav.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Network.RetrofitHelper
import com.example.weatherwizard.R
import com.example.weatherwizard.Repository
import com.example.weatherwizard.SharedPref
import com.example.weatherwizard.data.database.AppDb
import com.example.weatherwizard.data.database.LocalDataSource
import com.example.weatherwizard.data.model.FavoriteLocation
import com.example.weatherwizard.getAddressFromLocation
import com.example.weatherwizard.ui.theme.DarkPrimary
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapScreen(snackbarHostState: SnackbarHostState, fromSettings: Boolean,popBackStack:()->Unit) {

//    showFAB.value = false
    val context = LocalContext.current

    // Initialize Places API (outside ViewModel)
    Places.initializeWithNewPlacesApiEnabled(context, "AIzaSyCaj10hgcwGaosoYRyv79ppLviFJ9eMNmM")
    val placesClient: PlacesClient = Places.createClient(context)

    // Create ViewModel with custom factory
    val mapScreenFactory = MapScreenViewModel.MapScreenViewModelFactory(
        placesClient = placesClient,
        repository = Repository.getInstance(
            RemoteDataSource(RetrofitHelper.retrofitInstance), LocalDataSource(AppDb.getInstance(context).getDao(),AppDb.getInstance(context).getAlertDao(),AppDb.getInstance(context).getHomeDao())
//            WeatherLocalDataSource(WeatherDatabase.getInstance(context).getWeatherDao())
        )
    )
    val viewModel: MapScreenViewModel = viewModel(factory = mapScreenFactory)

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val predictions by viewModel.predictions.collectAsStateWithLifecycle()
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()

    val markerState = rememberMarkerState(
        position = LatLng(
            selectedLocation?.latitude ?: 31.2001,
            selectedLocation?.longitude ?: 29.9187,
        )
    )
    val cameraPositionState = rememberCameraPositionState{position = CameraPosition.fromLatLngZoom(markerState.position, 10f)}

    LaunchedEffect(Unit) {
        viewModel.message.collect {
           snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let {
            val newLatLng = LatLng(it.latitude, it.longitude)

            // Update marker position
            markerState.position = newLatLng

            // Move camera smoothly to new location
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(newLatLng, 12f))
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapType = MapType.HYBRID),
            onMapClick = { latLng ->
                markerState.position = latLng
                viewModel.updateSelectedLocation(latLng)
            }
        ) {
            Marker(
                state = markerState,
                title = "Selected Location",
                snippet = "Marker at selected location"
            )
        }

        PlacesAutocompleteTextField(

            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.White, shape = RoundedCornerShape(12.dp)),
            searchText = searchText,
            textFieldMaxLines = 1,
            predictions = predictions.map {
                AutocompletePlace(
                    placeId = it.placeId,
                    primaryText = it.getPrimaryText(null),
                    secondaryText = it.getSecondaryText(null)
                )
            },
            onQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onSelected = { autocompletePlace ->
                viewModel.onPlaceSelected(autocompletePlace.placeId)
            },
            selectedPlace = null

        )


        selectedLocation?.let { location ->

          val address = getAddressFromLocation(location)
            val favouriteLocation = FavoriteLocation(location.longitude, location.latitude, address)
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(colorResource(R.color.white))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = address, style = MaterialTheme.typography.titleMedium, color = colorResource(R.color.black), fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Latitude: ${location.latitude}")
                    Text(text = "Longitude: ${location.longitude}")
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                       onClick = {
                           if (fromSettings){
                               viewModel.selectUserLocation(favouriteLocation,context,popBackStack)
                           }
                           else{
                               val sharedPref= SharedPref.getInstance(context)

                               viewModel.insertFavouriteLocation(favouriteLocation,sharedPref.getLanguage()?:"en",sharedPref.getTempUnit()?:"metric")}
                                 }
                        ,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if(fromSettings) stringResource(R.string.select) else stringResource(R.string.add_to_favourites),
                            style = MaterialTheme.typography.titleMedium,
                            color = colorResource(R.color.white),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}