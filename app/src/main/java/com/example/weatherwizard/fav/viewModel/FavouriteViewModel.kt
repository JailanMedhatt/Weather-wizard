package com.example.weatherwizard.fav.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwizard.Repository
import com.example.weatherwizard.data.model.FavoriteLocation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
class FavouriteViewModel(val repository: Repository) : ViewModel()  {
    private val mutableMessage = MutableSharedFlow<String>()
    val message = mutableMessage.asSharedFlow()
    private var mutableLocations = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val locations = mutableLocations.asStateFlow()
    var deletedLocation:FavoriteLocation?=null
    class FavouriteViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory
    {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FavouriteViewModel(repository) as T
        }
    }
    fun deleteFavouriteLocation(location: FavoriteLocation) {
        viewModelScope.launch {
            deletedLocation=location
        repository.deleteLocation(location)
            mutableMessage.emit("Location Deleted Successfully")
        }
    }
    fun getFavouriteLocations() {
        viewModelScope.launch {
        repository.getLocations().catch {
            mutableMessage.emit(it.message.toString())
        }.collect{
            mutableLocations.value=it

        }}
    }
    fun restoreLocation(){
        viewModelScope.launch {
            repository.insertFavouriteLocation(deletedLocation!!)
        }
    }
}
