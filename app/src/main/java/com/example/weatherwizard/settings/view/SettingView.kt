package com.example.weatherwizard.settings.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.R
import com.example.weatherwizard.ui.theme.orange

@Composable
fun SettingScreen(){
    val settingItems = listOf(SettingItem(icon = R.drawable.language, description = "Language"
        , options = listOf("English", "Arabic")
    ),SettingItem(icon = R.drawable.temp, description = "Temp Unit"
        , options = listOf("Kelvin", "Celsius", "Fahrenheit"),
        ),
        SettingItem(icon = R.drawable.my_location, description = "Location"
            , options = listOf("Gps", "Map")
        ),
        SettingItem(icon = R.drawable.wind_setting, description = "Wind Speed Unit"
            , options = listOf("Meter/Sec", "mile/hour")
        )
    )
    Column (modifier = Modifier
        .fillMaxSize()
    ){
       settingItems.forEachIndexed { index, settingItem -> RadioButtonRow(options = settingItem.options, icon = settingItem.icon, description = settingItem.description)  }
    }
}

@Composable
fun RadioButtonRow(options :List<String>,icon :Int,description:String) {
//    val options = listOf("Option 1", "Option 2")
    var selectedOption = remember { mutableStateOf(options[0]) }

Column (Modifier.padding(vertical = 16.dp, horizontal = 8.dp).background(MyColors.secondary.color, shape = RoundedCornerShape(16.dp)).padding(vertical = 8.dp, horizontal = 8.dp)){
    Row{ Icon(painter = painterResource(icon), contentDescription = "",
        tint = Color.White
    )
    Text(description, color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp, start = 8.dp))}
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(8.dp)
            ) {
                RadioButton(
                    selected = (option == selectedOption.value),
                    onClick = { selectedOption.value = option },
                   colors = RadioButtonDefaults.colors(selectedColor = orange)
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 4.dp), color = Color.White
                )
            }
        }
    }}
}
class SettingItem(val icon :Int , val description: String , val options :List<String>)