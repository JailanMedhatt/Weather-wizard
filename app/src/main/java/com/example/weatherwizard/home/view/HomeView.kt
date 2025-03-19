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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.R
import com.example.weatherwizard.home.viewModel.HomeViewModel
import kotlinx.coroutines.delay


@ExperimentalGlideComposeApi
@Composable
fun HomeScreen(viewModel: HomeViewModel){
    viewModel.getTodayDateTime()
 LaunchedEffect(Unit) {
     while (viewModel.locationState.value.latitude == 0.0 && viewModel.locationState.value.longitude == 0.0) {
         delay(500) // Wait for location updates
     }
     Log.i("TAG", "HomeScreen: ")
        viewModel.getResponses()
     while (true) {
         delay(60000) // Update every second (adjust as needed)
         viewModel.getTodayDateTime()
     }
    }

    val currentWeather=viewModel.currentWeather.observeAsState()
    val hoursList=viewModel.immutableHoursList.observeAsState()
    val daysList=viewModel.immutableDaysList.observeAsState()
    val date =viewModel.immutableDate.observeAsState()
    Column (  modifier = Modifier
        .padding(horizontal = 32.dp)
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)
        .verticalScroll(rememberScrollState()),


    ){
        if(currentWeather.value==null){
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(align = Alignment.Center),color = Color.White
            )
        }
        else{
            Row(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween,){
                Column(Modifier.padding(top = 16.dp))  {
                    Row {   Text(text = " ${currentWeather.value?.name?:""}, ${currentWeather.value?.sys?.country}", color = Color.White , fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 16.dp, end = 8.dp))
                    Image(painter = painterResource(R.drawable.location),
                        contentDescription = "",Modifier.size(24.dp))
                    }

                Text(
                    text ="Feels like : ${currentWeather.value?.main?.feels_like} °K",
                    style = MaterialTheme.typography.headlineSmall, color = Color.White
                    , fontSize = 16.sp
                )}
//        Image(painter = painterResource(R.drawable.sun_rain)
//            , contentDescription = "",Modifier.size(200.dp))
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
//                GlideImage(
//            model = "https://openweathermap.org/img/wn/${currentWeather.value?.weather?.get(0)?.icon}@2x.png", contentDescription = "",
//            modifier = Modifier.size(100.dp))
                Image(painter = painterResource( R.drawable.cloudy_sunny), contentDescription = "",Modifier.size(150.dp))
                Row(Modifier.padding(top = 16.dp)) {
                    Text(
                        text = currentWeather.value?.main?.temp.toString(),
                        style = MaterialTheme.typography.headlineLarge, color = Color.White
                        , fontSize = 43.sp
                    )
                    Text(text = "\u00B0", color = Color.White, fontSize = 52.sp, modifier = Modifier.padding(start = 8.dp))
                    Text(text = "K", color = Color.White, fontSize = 22.sp, modifier = Modifier.padding(top = 8.dp))
                }
                Text(
                    text = currentWeather.value?.weather?.get(0)?.description ?: "",
                    style = MaterialTheme.typography.headlineLarge, color = Color.White
                    , fontSize = 26.sp, modifier = Modifier.padding(bottom = 16.dp)
                )
                Row (Modifier
                    .fillMaxWidth()
                    .background(color = MyColors.secondary.color, shape = RoundedCornerShape(20.dp))
                    .padding(vertical = 16.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
                    Column {

                         DetailedRow(0,R.drawable.cloud," Cloudiness :","${currentWeather.value?.clouds?.all.toString()}%")
                      DetailedRow(8,R.drawable.pressure," Pressure :","${currentWeather.value?.main?.pressure.toString()} hPa")

                    }
                    Column{

                        DetailedRow(0,R.drawable.wind," Wind speed :"
                            ,"${currentWeather.value?.wind?.speed.toString()} km/hr")

                         DetailedRow(8,R.drawable.humidity," Humidity :","${currentWeather.value?.main?.humidity.toString()}%")

                    }
                }



            }
            Text("Upcoming Hourly Temperatures :", color = Color.White, modifier = Modifier.padding(top = 32.dp), fontSize = 18.sp)
            LazyRow (Modifier.padding(top = 16.dp)){
                items(hoursList.value?.size?:0){
                        currentIndex->
                    val obj= hoursList.value!!.get(currentIndex)
                    HourCard(date = obj.dt_txt!!.substringAfter(" ").substringBeforeLast(":00"), icon = obj.weather?.get(0)!!.icon, temp = obj.main?.temp.toString())




                }
            }
          Row(Modifier.fillMaxWidth().padding(top = 32.dp, end = 16.dp,start =8.dp), horizontalArrangement = Arrangement.SpaceBetween,) {
              Text("Upcoming Daily Temperatures :", color = Color.White, fontSize = 18.sp)
          Image(painter = painterResource(R.drawable.calendar), contentDescription = "",Modifier.size(20.dp))
          }
            Column (Modifier.padding(top = 8.dp)){
                daysList.value?.forEach {
                        currentObj->

                    DayCard(date = currentObj.dt_txt!!.substringBefore(" "), icon = currentObj.weather?.get(0)!!.icon, temp = currentObj.main?.temp.toString())

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
fun HourCard(date:String, icon:String,temp:String){
    Column (Modifier
        .padding(end = 32.dp)
        .background(MyColors.secondary.color, shape = RoundedCornerShape(20.dp))
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
            Text(" °K",color = Color.White)
        }

    }
    }
@ExperimentalGlideComposeApi
@Composable
fun DayCard(date:String, icon:String,temp:String){


    Row (  Modifier
        .padding(bottom = 8.dp)
        .background(
            MyColors.secondary.color,
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
            Text(" °K",color = Color.White)
           // Icon(painter = )
        }


}}
