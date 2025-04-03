package com.example.weatherwizard.fav.map

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.data.Repository
import com.example.weatherwizard.data.SharedPref
import com.example.weatherwizard.data.model.FavoriteLocation

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MapScreenViewModel(private val placesClient: PlacesClient,private val repository: Repository): ViewModel() {

    class MapScreenViewModelFactory(private val placesClient: PlacesClient,private val repository: Repository) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MapScreenViewModel(placesClient,repository) as T
        }
    }

    private val mutableSearchText = MutableStateFlow("")
    val searchText: StateFlow<String> = mutableSearchText

    private val mutablePredictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions: StateFlow<List<AutocompletePrediction>> = mutablePredictions

    private val mutableSelectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> get() = mutableSelectedLocation

    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()

    //private val locationBias: LocationBias = RectangularBounds.newInstance(LatLng(39.9, -105.5), // SW lat, lng LatLng(40.1, -105.0)  // NE lat, lng)

    // Update search query and fetch predictions
    fun onSearchQueryChanged(query: String) {
        mutableSearchText.value = query
        fetchPredictions(query)
    }

    //Fetch place details when a prediction is selected
    fun onPlaceSelected(placeId: String) {
        fetchPlaceDetails(placeId)
        mutableSearchText.value = "" // Clear search text after selection
        mutablePredictions.value = emptyList() // Clear predictions
    }

    // Fetch autocomplete predictions
    private fun fetchPredictions(query: String) {
        if (query.isEmpty()) {
            mutablePredictions.value = emptyList() // Clear predictions when input is empty
            return
        }

        viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    mutablePredictions.value = response.autocompletePredictions
                }
                .addOnFailureListener { exception ->
                    Log.e("MapViewModel", "Error fetching predictions: ${exception.message}")
                }
        }
    }

    // Fetch place details
    fun fetchPlaceDetails(placeId: String) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                response.place.latLng?.let { latLng ->
                    mutableSelectedLocation.value = Location("").apply {
                        latitude = latLng.latitude
                        longitude = latLng.longitude
                    }

                }
            }
            .addOnFailureListener { exception ->
                Log.e("MapViewModel", "Error fetching place details: ${exception.message}")
            }
    }

    fun updateSelectedLocation(latLng: LatLng) {
        mutableSelectedLocation.value = Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
    }
    fun insertFavouriteLocation(favouriteLocation: FavoriteLocation,language: String, unit: String){
        viewModelScope.launch {
            var favWeatherDetails: FavWeatherDetails? = null

            val currentWeatherFlow = repository.getCurrentWeather(
                favouriteLocation.latitude, favouriteLocation.longitude, language, unit
            )

            val hoursWeatherFlow = repository.getHoursResponse(
                favouriteLocation.latitude, favouriteLocation.longitude, language, unit
            )

            combine(currentWeatherFlow, hoursWeatherFlow) { weatherResponse, hoursResponse ->
                FavWeatherDetails(favouriteLocation,weatherResponse, hoursResponse)
            }.collect { favDetails ->
                favWeatherDetails = favDetails
            }
            repository.insertFavouriteLocation(favWeatherDetails!!)
//            repository.insertFavouriteLocation(favouriteLocation)
            mutableMessage.emit("Location Added Successfully!")
        }


    }
    fun selectUserLocation(location: FavoriteLocation,context: Context,popBackStack:()->Unit){
        viewModelScope.launch {
            mutableMessage.emit("Location Selected Successfully!")
        val sharedPref = SharedPref.getInstance(context)
        sharedPref.setLongitudeAndLatitude(location.longitude,location.latitude)
        sharedPref.setGpsSelected(false)
        delay(500)
        popBackStack.invoke()}

    }

//    fun insertFavouriteLocation(lat: Double, lon: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val favouriteLocation = FavouriteLocation(latitude = lat, longitude = lon)
//                repository.insertFavouriteLocation(favouriteLocation)
//                mutableMessage.emit("Location Added Successfully!")
//            } catch (ex: Exception){
//                mutableMessage.emit("Couldn't Add Location To Favourites ${ex.message}")
//            }
//        }
//    }

}