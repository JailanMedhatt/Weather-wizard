package com.example.weatherwizard.settings.view
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherwizard.MyColors
import com.example.weatherwizard.R
import com.example.weatherwizard.SharedPref
import com.example.weatherwizard.settings.viewModel.SettingsViewModel
import com.example.weatherwizard.ui.theme.orange
@Composable
fun SettingScreen(){
    val viewModel= SettingsViewModel()
    val context = LocalContext.current
    val sharedPref= SharedPref.getInstance(context)

        val selectedLanguage = remember { mutableStateOf(sharedPref.getLanguage()?:"en") }
        val selectedTempUnit = remember {  mutableStateOf( sharedPref.getTempUnit()?:"metric")}
        val selectedWindSpeedUnit = remember { mutableStateOf(sharedPref.getWindSpeedUnit()?:"Meter/Sec") }

    val isArabic = selectedLanguage.value == "ar" || selectedLanguage.value == "العربية"

    selectedLanguage.value = if (isArabic) "العربية" else "English"
    selectedTempUnit.value = when (selectedTempUnit.value) {
        "metric", "Celsius", "درجة مئوية" -> if (isArabic) "درجة مئوية" else "Celsius"
        "imperial", "Fahrenheit", "فهرنهايت" -> if (isArabic) "فهرنهايت" else "Fahrenheit"
        else -> if (isArabic) "كلفن" else "Kelvin"
    }

    selectedWindSpeedUnit.value = if (selectedTempUnit.value == (if (isArabic) "فهرنهايت" else "Fahrenheit")) {
        if (isArabic) "ميل/ساعة" else "Mile/Hour"
    } else {
        if (isArabic) "متر/ثانية" else "Meter/Sec"
    }



    val settingItems = listOf(SettingItem(icon = R.drawable.language, description = stringResource(R.string.language)
        , options = listOf(stringResource(R.string.english), stringResource(R.string.arabic)),selectedLanguage
   ,action ={selectedOption-> viewModel.changeAppLanguage(selectedOption,context) } ),
        SettingItem(
        icon = R.drawable.temp, description = stringResource(R.string.temp_unit),
        options = listOf(stringResource(R.string.kelvin),
            stringResource(R.string.celsius), stringResource(R.string.fahrenheit)
        ),selectedTempUnit,action ={selectedOption-> viewModel.changeTempUnit(selectedOption,context) }
    ),
        SettingItem(icon = R.drawable.my_location, description = stringResource(R.string.location)
            , options = listOf(stringResource(R.string.gps), stringResource(R.string.map)),selectedLanguage
        ),
        SettingItem(icon = R.drawable.wind_setting, description = stringResource(R.string.wind_speed_unit)
            , options = listOf(stringResource(R.string.meter_sec),
                stringResource(R.string.mile_hour)),selectedWindSpeedUnit, action = null
        )
    )
    Column (modifier = Modifier
        .fillMaxSize()
    ){
       settingItems.forEachIndexed { index, settingItem -> RadioButtonRow(options = settingItem.options, icon = settingItem.icon, description = settingItem.description,
           action =settingItem.action,settingItem.savedOption)  }
    }


}

@Composable
fun RadioButtonRow(options :List<String>,icon :Int,description:String, action: ((option:String)->Unit)?,savedOption:MutableState<String>) {
Column (Modifier
    .padding(vertical = 16.dp, horizontal = 8.dp)
    .background(MyColors.secondary.color, shape = RoundedCornerShape(16.dp))
    .padding(vertical = 8.dp, horizontal = 8.dp)){
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
                    selected = (option == savedOption.value),
                    onClick = {

                        if (action != null) {
                       savedOption.value = option
                            savedOption.value= option

                        action(option)
                    }},
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
class SettingItem(val icon :Int , val description: String , val options :List<String>,val savedOption: MutableState<String>, val action: ((option:String)->Unit)?=null)
