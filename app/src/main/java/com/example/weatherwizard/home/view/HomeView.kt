package com.example.weatherwizard.home.view
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.weatherwizard.Pojos.CurrentWeatherResponse
import com.example.weatherwizard.Pojos.FavWeatherDetails
import com.example.weatherwizard.R
import com.example.weatherwizard.Response
import com.example.weatherwizard.data.SharedPref
import com.example.weatherwizard.home.viewModel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json

@ExperimentalGlideComposeApi
@Composable
fun HomeScreen(viewModel: HomeViewModel,obj: String){
    viewModel.getTodayDateTime()
    val sharedPref= SharedPref.getInstance(LocalContext.current)
    val unit=sharedPref.getTempUnit()?:"metric"
    var unitSpeed=sharedPref.getWindSpeedUnit()?:"Meter/Sec"
    val isGpsSelected = sharedPref.getGpsSelected()
      val language=sharedPref.getLanguage()?:"en"
    val MyObj = if(obj!="{}") Json.decodeFromString<FavWeatherDetails>(obj) else ""
    if(language=="ar"){
        unitSpeed= if(unitSpeed=="Meter/Sec") "متر/ثانية" else "ميل/ساعة"

    }
    LaunchedEffect(Unit) {

     viewModel.setTempUnit(unit)
        if(MyObj==""){

            if(isGpsSelected){
                sharedPref.setLongitudeAndLatitude(viewModel.locationState.value.longitude, viewModel.locationState.value.latitude)


     while (viewModel.locationState.value.latitude == 0.0 && viewModel.locationState.value.longitude == 0.0) {
         delay(500) // Wait for location updates
     }
        viewModel.getResponses(0.0, 0.0, true)}
            else{
                val pair = sharedPref.getLatitudeAndLongitude()
                viewModel.getResponses(
                    longitude = pair.second,
                    latitude = pair.first,
                    fromHome = true
                )
            }
        }
        else{
            viewModel.getResponses(longitude = (MyObj as FavWeatherDetails).favoriteLocation.longitude, latitude =  (MyObj as FavWeatherDetails).favoriteLocation.latitude,fromHome=false)
        }
     while (true) {
         delay(60000) // Update every second (adjust as needed)
         viewModel.getTodayDateTime() }
    }
    val currentWeatherResponse= viewModel.immutableCurrentWeatherResponse.collectAsStateWithLifecycle()
    val daysResponse= viewModel.immutableDaysResponse.collectAsStateWithLifecycle()
    val hoursResponse= viewModel.immutableHoursResponse.collectAsStateWithLifecycle()
    val date =viewModel.immutableDate.observeAsState()
    Column (  modifier = Modifier
        .padding(horizontal = 32.dp)
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
        .verticalScroll(rememberScrollState()),


    ){
        if(currentWeatherResponse.value is Response.Loading||hoursResponse.value is Response.Loading){
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center),color = Color.White
            )
        }
        else if (currentWeatherResponse.value is Response.CurrentWeatherSuccess&&hoursResponse.value is Response.HoursOrDaysSuccess){
            val currentWeather= (currentWeatherResponse.value as Response.CurrentWeatherSuccess).data
            val hoursList= (hoursResponse.value as Response.HoursOrDaysSuccess).data
            val daysList= (daysResponse.value as Response.HoursOrDaysSuccess).data
            MyUi(currentWeather,hoursList,daysList,unit,unitSpeed,date,language,viewModel)
            }

        else{
            if(MyObj==""){
                LaunchedEffect(Unit) {
                    viewModel.getDetails()
                }
                if(currentWeatherResponse.value is Response.CurrentWeatherSuccess&&hoursResponse.value is Response.HoursOrDaysSuccess){
                    val currentWeather= (currentWeatherResponse.value as Response.CurrentWeatherSuccess).data
                    val hoursList= (hoursResponse.value as Response.HoursOrDaysSuccess).data
                    val daysList= (daysResponse.value as Response.HoursOrDaysSuccess).data
                    MyUi(currentWeather,hoursList,daysList,unit,unitSpeed,date,language,viewModel)}
                  else if(currentWeatherResponse.value is Response.Error&&hoursResponse.value is Response.Error){
                    Log.i("Tag", "HomeScreen:${(currentWeatherResponse.value as Response.Error).message} ")

                    Text(text = (currentWeatherResponse.value as Response.Error).message, modifier = Modifier.padding(top = 16.dp), color = Color.White)
                }
                else {
                    CircularProgressIndicator()
                }


            }
            else{
                Log.i("TAG", "HomeScreen: ${MyObj.toString()}")
                LaunchedEffect(Unit){
                    viewModel.handleResponses(MyObj as FavWeatherDetails)
                }
          if(currentWeatherResponse.value is Response.CurrentWeatherSuccess&&hoursResponse.value is Response.HoursOrDaysSuccess){
                val currentWeather= (currentWeatherResponse.value as Response.CurrentWeatherSuccess).data
                val hoursList= (hoursResponse.value as Response.HoursOrDaysSuccess).data
                val daysList= (daysResponse.value as Response.HoursOrDaysSuccess).data
                MyUi(currentWeather,hoursList,daysList,unit,unitSpeed,date,language,viewModel)}
                else{
              CircularProgressIndicator()
          }
            }
        }
    }
}
@Composable
fun DetailedRow(topPadding :Int, icon:Int,description:String,value:String){
    Row(modifier = Modifier.padding(top = topPadding.dp)) {
        Image(painter = painterResource(icon), contentDescription = "",Modifier.size(26.dp))
        Text(text = description,color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 16.sp)
        Text(text = value,color = Color.White, modifier = Modifier.padding(top = 8.dp), fontSize = 16.sp)
    }
}

@ExperimentalGlideComposeApi
@Composable
fun HourCard(date:String, icon:String,temp:String, unit:String){
    Column (Modifier
        .padding(end = 32.dp)
        .background( MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
        .padding(vertical = 8.dp, horizontal = 8.dp)
        .wrapContentSize(Alignment.Center))
    {
       Row(Modifier.padding(bottom = 8.dp)){ Text(text =date,color = Color.White, fontSize = 17.sp)
           Image(painter = painterResource(R.drawable.clock),
               contentDescription = "",Modifier
                   .size(22.dp)
                   .padding(start = 8.dp, bottom = 4.dp))
       }

        GlideImage(
            model = "https://openweathermap.org/img/wn/${icon}@2x.png", contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .padding(start = 8.dp, bottom = 16.dp))
        Row(Modifier.padding(start = 8.dp)) {
            Text(text = temp,color = Color.White)
            TempUnitText(unit)
        }

    }
    }
@ExperimentalGlideComposeApi
@Composable
fun DayCard(date:String, icon:String,temp:String,unit: String){


    Row (  Modifier
        .padding(bottom = 8.dp)
        .background(
            MaterialTheme.colorScheme.secondary,
            shape = RoundedCornerShape(20.dp)
        )
        .fillMaxWidth()
        .padding(vertical = 8.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween)
    {
        Text(text =date,color = Color.White, fontSize = 17.sp, modifier = Modifier.padding(top = 16.dp))



        GlideImage(
            model = "https://openweathermap.org/img/wn/${icon}@2x.png", contentDescription = "",
            modifier = Modifier
                .size(50.dp)
                .padding(start = 8.dp))
        Row(Modifier.padding(start = 8.dp, top = 16.dp)) {
            Text(text = temp,color = Color.White)
            TempUnitText(unit)
           // Icon(painter = )
        }


}}

@Composable
fun TempUnitText(unit :String,fontSize: Int=16,circleSize :Int=22,topPadding: Int=0,startPadding:Int=4){
    if(unit=="metric"){

            Text(text = "\u00B0", color = Color.White, fontSize = circleSize.sp, modifier = Modifier.padding(start = startPadding.dp))
                    Text(text = stringResource(R.string.c), color = Color.White, fontSize = fontSize.sp, modifier = Modifier.padding(top = topPadding.dp))
    }
    else if(unit=="imperial"){
        Text(text = "\u00B0", color = Color.White, fontSize = circleSize.sp, modifier = Modifier.padding(start = startPadding.dp))
        Text(text = stringResource(R.string.f), color = Color.White, fontSize = fontSize.sp, modifier = Modifier.padding(top = topPadding.dp))
    }
    else{
        Text(text = "\u00B0", color = Color.White, fontSize = circleSize.sp, modifier = Modifier.padding(start = startPadding.dp))
        Text(text = stringResource(R.string.k), color = Color.White, fontSize = fontSize.sp, modifier = Modifier.padding(top = topPadding.dp))

    }
}
@ExperimentalGlideComposeApi
@Composable
fun MyUi(
    currentWeather:CurrentWeatherResponse,
    hoursList:List<CurrentWeatherResponse>,
    daysList:List<CurrentWeatherResponse>,
    unit:String,
    unitSpeed:String,
    date: State<String?>,
    language:String,viewModel: HomeViewModel
){
    Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,){
        Column(Modifier.padding(top = 16.dp))  {
            Row {   Text(text = " ${currentWeather.name?:""}, ${currentWeather.sys?.country}", color = Color.White , fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp))
                Image(painter = painterResource(R.drawable.location),
                    contentDescription = "",Modifier.size(24.dp))
            }
            Row {
                Text(

                    text = stringResource(R.string.feels_like) ,
                    style = MaterialTheme.typography.headlineSmall, color = Color.White
                    , fontSize = 16.sp
                )
                Text(

                    text = if(language=="ar") viewModel.convertToArabicNumbers(currentWeather.main?.feels_like?.toInt().toString()) else "${currentWeather.main?.feels_like?.toInt()}",
                    style = MaterialTheme.typography.headlineSmall, color = Color.White
                    , fontSize = 16.sp
                )
                TempUnitText(unit = unit, topPadding = 4)
            }

        }

        Column(Modifier.padding(top = 16.dp)) {
            Row{
                Text(text = stringResource(R.string.today), color = Color.White , fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 16.dp, end = 8.dp))
                Image(painter = painterResource(R.drawable.clock),
                    contentDescription = "",Modifier.size(23.dp))}
            Text(
                text = date.value?:"",
                style = MaterialTheme.typography.headlineSmall, color = Color.White
                , fontSize = 16.sp
            )
        }
    }


    Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment =  Alignment.CenterHorizontally) {
        Image(painter = painterResource( getWeatherIcon(currentWeather.weather?.get(0)?.main!!)), contentDescription = "",Modifier.size(150.dp))
        Row(Modifier.padding(top = 16.dp)) {
            Text(
                text = if(language=="ar") viewModel.convertToArabicNumbers(  currentWeather.main?.temp?.toInt().toString()) else currentWeather.main?.temp?.toInt().toString(),
                style = MaterialTheme.typography.headlineLarge, color = Color.White
                , fontSize = 43.sp
            )
            TempUnitText(unit, fontSize = 22, circleSize = 40, startPadding = 8, topPadding = 8)
        }
        Text(
            text = currentWeather.weather?.get(0)?.description ?: "",
            style = MaterialTheme.typography.headlineLarge, color = Color.White
            , fontSize = 26.sp, modifier = Modifier.padding(bottom = 16.dp)
        )
        Row (Modifier
            .fillMaxWidth()
            .background(color =  MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Column {

                DetailedRow(0,R.drawable.cloud,
                    " : ","${if(language=="ar") viewModel.convertToArabicNumbers(currentWeather.clouds?.all.toString()) else currentWeather.clouds?.all.toString()} % ")
                DetailedRow(8,R.drawable.pressure,
                    " : ","${if (language=="ar")viewModel.convertToArabicNumbers(currentWeather.main?.pressure.toString()) else currentWeather.main?.pressure.toString()} "+ stringResource(R.string.hpa)
                )

            }

            Column{
                //${stringResource(R.string.wind_speed)}

                DetailedRow(0,R.drawable.wind,"  : "
                    ,"${if (language=="ar")viewModel.convertToArabicNumbers(currentWeather.wind?.speed.toString()) else currentWeather.wind?.speed.toString()} $unitSpeed")

                DetailedRow(8,R.drawable.humidity, " : ","${ if (language=="ar")viewModel.convertToArabicNumbers(currentWeather.main?.humidity.toString()) else currentWeather.main?.humidity.toString() } % ")

            }
        }
    }

    Text(stringResource(R.string.upcoming_hourly_temperatures), color = Color.White, modifier = Modifier.padding(top = 32.dp), fontSize = 18.sp)
    LazyRow (Modifier.padding(top = 16.dp)){
        items(hoursList.size){
                currentIndex->
            val obj= hoursList.get(currentIndex)
            HourCard(date = if (language=="ar")viewModel.convertToArabicNumbers(obj.dt_txt!!.substringAfter(" ").substringBeforeLast(":00")) else obj.dt_txt!!.substringAfter(" ").substringBeforeLast(":00"), icon = obj.weather?.get(0)!!.icon, temp = if(language=="ar") viewModel.convertToArabicNumbers(obj.main?.temp?.toInt().toString()) else obj.main?.temp?.toInt().toString(),unit)
        }
    }



    Row(Modifier
        .fillMaxWidth()
        .padding(top = 32.dp, end = 16.dp, start = 8.dp), horizontalArrangement = Arrangement.SpaceBetween,) {
        Text(stringResource(R.string.upcoming_daily_temperatures), color = Color.White, fontSize = 18.sp)
        Image(painter = painterResource(R.drawable.calendar), contentDescription = "",Modifier.size(20.dp))
    }
    Column (Modifier.padding(top = 8.dp)){
        daysList.forEach {
                currentObj->

            DayCard(date = currentObj.dt_txt!!.substringBefore(" "), icon = currentObj.weather?.get(0)!!.icon, temp = if(language=="ar") viewModel.convertToArabicNumbers( currentObj.main?.temp?.toInt().toString()) else currentObj.main?.temp?.toInt().toString(),unit)
        }
    }
}
fun getWeatherIcon(weatherMain: String): Int {
    return when (weatherMain) {
        "Clouds" -> R.drawable.cloudy_sunny
        "Clear" -> R.drawable.clear_sunny
        "Rain" -> R.drawable.rain_weather
        "Snow" -> R.drawable.snow_weather
        "Thunderstorm" -> R.drawable.thunder_weather
        "Drizzle" -> R.drawable.rain_weather
        "Atmosphere" -> R.drawable.atmosphere_weather
        else -> R.drawable.cloudy_weather
    }
}