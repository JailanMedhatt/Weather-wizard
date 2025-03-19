package com.example.weatherwizard.home.viewModel
import android.location.Location
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Repository
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

 
class HomeViewModel(private val repo :Repository):ViewModel() {
     lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     lateinit var locationState: MutableState<Location>
  private  var currentWeatherResponse: MutableLiveData<CurrentWeatherResponse?> = MutableLiveData()
    val currentWeather:LiveData<CurrentWeatherResponse?> =currentWeatherResponse
    private  var hoursList: MutableLiveData<List<CurrentWeatherResponse>> = MutableLiveData()
    val immutableHoursList:LiveData<List<CurrentWeatherResponse>> =hoursList
    private  var daysList: MutableLiveData<List<CurrentWeatherResponse>> = MutableLiveData()
    val immutableDaysList:LiveData<List<CurrentWeatherResponse>> =daysList
   private var date :MutableLiveData<String> = MutableLiveData()
    val immutableDate : LiveData<String> = date
    fun getResponses(){
        viewModelScope.launch {

            if(locationState.value.latitude !=0.0&&locationState.value.longitude!=0.0){
                getCurrentWeather()
              //  getHoursForEachDay(hoursList.value?: listOf())
                getHoursAndDays()
                Log.i("TAG", "get responses: ")

        }else{
            Log.i("TAG", "get responses: else")
        }
        }
    }
    suspend fun getCurrentWeather(){
        val response = repo.getCurrentWeather(latitude = locationState.value.latitude,logitude = locationState.value.longitude)
        if (response.isSuccessful) {
            currentWeatherResponse.postValue(response.body()!!)
            Log.i("TAG", "getCurrentWeather: ")

        }
        else{
            Log.i("Zz", response.message())
        }

    }
   suspend fun getHoursAndDays(){
                val res= repo.getHoursResponse(latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude)
                if(res.isSuccessful){
                 val list=   getHoursForToday(res.body()!!.list)
                    hoursList.postValue(list)
                     var list2=   getHoursForEachDay(res.body()!!.list)
                    list2=list2.asSequence().map { it.copy(dt_txt = getDayName(it.dt_txt!!)) }.toList()
                    daysList.postValue(list2)

                }

    }
    fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return "Invalid Date"

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // "EEEE" gives full day name
        return dayFormat.format(date)
    }
    fun getHoursForToday(hours:List<CurrentWeatherResponse>):List<CurrentWeatherResponse>{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
        val list = hours.asSequence().filter { item-> item.dt_txt!!.startsWith(today) }.toList()

        return list
    }
    fun getTodayDateTime(){
        viewModelScope.launch {
        val sdf = SimpleDateFormat("yyyy-MM-dd \n  HH:mm", Locale.getDefault())
        val today = sdf.format(Date())
        date.postValue(today)}
    }
    fun getHoursForEachDay(hours:List<CurrentWeatherResponse>):List<CurrentWeatherResponse>{
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
       var list:MutableList<CurrentWeatherResponse> = mutableListOf()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        if(hours.isNotEmpty()){
            for (i in 0..5) {
                val date = sdf.format(calendar.time)
                var filteredlist=hours.asSequence().filter { item -> item.dt_txt!!.startsWith(date) }.toList()
                if(filteredlist.isNotEmpty()){
                    list.add(filteredlist[0])
                }

                calendar.add(Calendar.DAY_OF_YEAR, 1)

            }
        }
        Log.d("dd",list.toString())
        return list
    }


    class MyFactory(private val repo: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repo) as T
        }}
}