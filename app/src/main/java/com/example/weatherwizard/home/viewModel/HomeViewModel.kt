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
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.data.Repository
import com.example.weatherwizard.Response
import com.example.weatherwizard.data.model.FavoriteLocation
import com.example.weatherwizard.home.model.DetailsModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeViewModel(private val repo : Repository):ViewModel() {
     lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     lateinit var locationState: MutableState<Location>
     private   var _languageState: MutableStateFlow<String> = MutableStateFlow("en")
    private   var _unitState: MutableStateFlow<String> = MutableStateFlow("metric")
    private var date :MutableLiveData<String> = MutableLiveData()
    val immutableDate : LiveData<String> = date
    private var CurrentWeatherresponse = MutableStateFlow<Response>(Response.Loading)
    val immutableCurrentWeatherResponse =CurrentWeatherresponse.asStateFlow()
    private var hoursResponse = MutableStateFlow<Response>(Response.Loading)
    val immutableHoursResponse =hoursResponse.asStateFlow()
    private var daysResponse = MutableStateFlow<Response>(Response.Loading)
    val immutableDaysResponse =daysResponse.asStateFlow()
    private var hoursList :Flow<List<CurrentWeatherResponse>> = flowOf()

    fun setLanguage(language: String) {
        _languageState.value = language
    }
    fun setTempUnit(unit: String) {
        _unitState.value = unit
    }

    fun getResponses(longitude: Double, latitude: Double, fromHome: Boolean){
        viewModelScope.launch {
                getCurrentWeather(longitude,latitude)
                getHours(longitude,latitude)
                getDays()


            if(fromHome){
                combine(
                    immutableCurrentWeatherResponse,
                    immutableHoursResponse
                ) { currentWeather, hoursOrDays ->
                    if (currentWeather is Response.CurrentWeatherSuccess &&
                        hoursOrDays is Response.HoursOrDaysSuccess) {

                        val weatherData = currentWeather.data
                        val hoursListData = hoursList.first()
                        insertDetails(DetailsModel(longitude, latitude, weatherData, hoursList = hoursListData))
                    }
                }.collect{}
            }

        }
    }
    suspend fun getDays(){
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = sdf.format(Date())
      //  Log.i("TAG", "getDays: ${hoursList.first().size}")
      hoursList.map { response ->
            response
                .filter { !it.dt_txt?.substringBefore(" ")!!.startsWith(today) }
                .map { item -> item.copy(dt_txt = getDayName(item.dt_txt?.substringBefore(" ")!!)) }
                .groupBy { item -> item.dt_txt }
                .map { (_, items) -> items.first() }
        }.collect { daysResponse.value = Response.HoursOrDaysSuccess(it)

            Log.i("TAG", "getDays: ${it.size}")
        }
     //   Log.i("TAG", "getDays finished: ${hoursList.first().size} ")



    }
    suspend fun getCurrentWeather(longitude: Double,latitude: Double){
        val myLongitude=if(longitude==0.0) locationState.value.longitude else longitude
        val myLatitude=if(latitude==0.0) locationState.value.latitude else latitude
         repo.getCurrentWeather(latitude = myLatitude,logitude = myLongitude,_languageState.value,_unitState.value).catch {
             CurrentWeatherresponse.value=Response.Error(it.message.toString())
         }.collect{
            CurrentWeatherresponse.value=Response.CurrentWeatherSuccess(it)
        }


    }

    suspend fun handleResponses(obj : FavWeatherDetails){

        CurrentWeatherresponse.value = Response.CurrentWeatherSuccess(obj.currentWeatherResponse)
        hoursList= flowOf(obj.hoursList)
        Log.i("TAG", "handleResponses: ${obj.hoursList.size}  ")
        // Process the hours list
        val list: Flow<List<CurrentWeatherResponse>> = flowOf(obj.hoursList).map { it.take(8) }

        list.collect { filteredList ->
          //  hoursList = flowOf(filteredList) // ✅ Update hoursList properly
            hoursResponse.value = Response.HoursOrDaysSuccess(filteredList) // ✅ Assign the collected value
        }
        getDays()

    }
   suspend fun getHours(longitude: Double,latitude: Double){
       val myLongitude=if(longitude==0.0) locationState.value.longitude else longitude
       val myLatitude=if(latitude==0.0) locationState.value.latitude else latitude
       repo.getHoursResponse(latitude = myLatitude,
                    longitude = myLongitude,_languageState.value, units = _unitState.value).catch {
                        hoursResponse.value=Response.Error(it.message.toString())
       }
           .map {
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
    suspend fun insertDetails(obj: DetailsModel) {
        repo.getDetails().firstOrNull()?.let { existingDetails ->existingDetails.forEachIndexed { index, detailsModel ->   repo.deleteDetails(detailsModel) }
        }
        repo.insertDetails(obj)
    }
    suspend fun getDetails(){
            repo.getDetails().catch { Log.i("ERRORR", "getDetails: ${it.message}")
                CurrentWeatherresponse.value=Response.Error("No Data")
                hoursResponse.value=Response.Error("No Data") }.collect{
                if(!it.isNullOrEmpty()){
                handleResponses(FavWeatherDetails(FavoriteLocation(it.get(0).longitude,it.get(0).latitute,""),it.get(0).currentWeatherResponse,it.get(0).hoursList))}
           else{CurrentWeatherresponse.value=Response.Error("No Data")
               hoursResponse.value=Response.Error("No Data")} }

    }
    fun convertToArabicNumbers(number: String): String {
        val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
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