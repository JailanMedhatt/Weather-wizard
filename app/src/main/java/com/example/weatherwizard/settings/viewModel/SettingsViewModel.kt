package com.example.weatherwizard.settings.viewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weatherwizard.data.SharedPref

class SettingsViewModel {
    fun changeAppLanguage(language: String, context: Context) {

        val sharedPref= SharedPref.getInstance(context)
        sharedPref.setLanguage(if (language == "Arabic"||language=="العربية") "ar"
        else if(language=="English"||language=="الإنجليزية") "en"
        else "Default")
        Log.i("tag", "changeAppLanguage: ${sharedPref.getLanguage()}")
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }



    fun changeTempUnit(unit: String, context: Context) {
        val sharedPref= SharedPref.getInstance(context)
        if(unit=="Celsius"||unit=="درجة مئوية"){
            sharedPref.setTempUnit("metric")
            sharedPref.setWindSpeedUnit("Meter/Sec")
        }
        else if(unit=="Fahrenheit"||unit=="فهرنهايت"){
            sharedPref.setTempUnit("imperial")
            sharedPref.setWindSpeedUnit("Mile/Hour")
        }
        else{
            sharedPref.setTempUnit("standard")
            sharedPref.setWindSpeedUnit("Meter/Sec")
    }}


    fun changeLocationSelection(selectedLocation: String, context: Context,onNavigateToMap :()->Unit) {
        val sharedPref= SharedPref.getInstance(context)
        if(selectedLocation=="Map"||selectedLocation=="الخريطة"){
            onNavigateToMap.invoke()
        }
        else{
            sharedPref.setGpsSelected(true)
        }
    }
    fun changeTheme(theme: String, context: Context) {
        val selectedTheme = when (theme) {
            "Dark","داكن" -> "Dark"
            "Light","فاتح" -> "Light"
            else -> "Default"
        }
        val sharedPref= SharedPref.getInstance(context)
        sharedPref.setTheme(selectedTheme)
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)
    }
}