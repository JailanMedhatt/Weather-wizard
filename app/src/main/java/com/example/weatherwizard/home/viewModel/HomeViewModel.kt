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
import com.example.weatherwizard.Response
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

 
class HomeViewModel(private val repo :Repository):ViewModel() {
     lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     lateinit var locationState: MutableState<Location>
   private var date :MutableLiveData<String> = MutableLiveData()
    val immutableDate : LiveData<String> = date
    private var CurrentWeatherresponse = MutableStateFlow<Response>(Response.Loading)
    val immutableCurrentWeatherResponse =CurrentWeatherresponse.asStateFlow()
    private var hoursResponse = MutableStateFlow<Response>(Response.Loading)
    val immutableHoursResponse =hoursResponse.asStateFlow()
    private var daysResponse = MutableStateFlow<Response>(Response.Loading)
    val immutableDaysResponse =daysResponse.asStateFlow()
   lateinit private var hoursList :Flow<List<CurrentWeatherResponse>>

    fun getResponses(){
        viewModelScope.launch {

            if(locationState.value.latitude !=0.0&&locationState.value.longitude!=0.0){
                getCurrentWeather()
              //  getHoursForEachDay(hoursList.value?: listOf())
                getHours()
                Log.i("TAG", "get responses: ")
                getDays()

        }else{
            Log.i("TAG", "get responses: else")
        }
        }
    }
    suspend fun getDays(){
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
      hoursList.map { response ->
            response
                .filter { !it.dt_txt?.substringBefore(" ")!!.startsWith(today) }
                .map { item -> item.copy(dt_txt = getDayName(item.dt_txt?.substringBefore(" ")!!)) }
                .groupBy { item -> item.dt_txt }
                .map { (_, items) -> items.first() }
        }.collect { daysResponse.value = Response.HoursOrDaysSuccess(it)

            Log.i("TAG", "getDays: ${it.size}")
        }



    }
    suspend fun getCurrentWeather(){
         repo.getCurrentWeather(latitude = locationState.value.latitude,logitude = locationState.value.longitude).collect{
            CurrentWeatherresponse.value=Response.CurrentWeatherSuccess(it)
        }


    }
   suspend fun getHours(){
                repo.getHoursResponse(latitude = locationState.value.latitude,
                    longitude = locationState.value.longitude).map {
                      hoursList= flowOf(it)
                        it.take(8)

                    }.collect{
                        hoursResponse.value=Response.HoursOrDaysSuccess(it)

                }

    }
    fun getDayName(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateString) ?: return "Invalid Date"

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // "EEEE" gives full day name
        return dayFormat.format(date)
    }

    fun getTodayDateTime(){
        viewModelScope.launch {
        val sdf = SimpleDateFormat("yyyy-MM-dd \n  HH:mm", Locale.getDefault())
        val today = sdf.format(Date())
        date.postValue(today)}

    }
    class MyFactory(private val repo: Repository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repo) as T
        }}
//    fun getHoursForEachDay(hours:List<CurrentWeatherResponse>):List<CurrentWeatherResponse>{
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val calendar = Calendar.getInstance()
//       var list:MutableList<CurrentWeatherResponse> = mutableListOf()
//        calendar.add(Calendar.DAY_OF_YEAR, 1)
//        if(hours.isNotEmpty()){
//            for (i in 0..5) {
//                val date = sdf.format(calendar.time)
//                var filteredlist=hours.asSequence().filter { item -> item.dt_txt!!.startsWith(date) }.toList()
//                if(filteredlist.isNotEmpty()){
//                    list.add(filteredlist[0])
//                }
//
//                calendar.add(Calendar.DAY_OF_YEAR, 1)
//
//            }
//        }
//        Log.d("dd",list.toString())
//        return list
//    }
//    fun getHoursForToday(hours:List<CurrentWeatherResponse>):List<CurrentWeatherResponse>{
//        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//        val today = sdf.format(Date())
//        val list = hours.asSequence().filter { item-> item.dt_txt!!.startsWith(today) }.toList()
//
//        return list
//    }


}