package com.example.weatherwizard.settings.viewModel

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import com.example.weatherwizard.SharedPref
import java.util.Locale

class SettingsViewModel {
    fun changeAppLanguage(language: String, context: Context) {
        val locale = Locale(if (language == "Arabic"||language=="العربية") "en" else "en")
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        val sharedPref= SharedPref.getInstance(context)
        sharedPref.setLanguage(if (language == "Arabic"||language=="العربية") "ar" else "en")
        Log.i("tag", "changeAppLanguage: ${sharedPref.getLanguage()}")
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        Runtime.getRuntime().exit(0)// Apply the language change
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
}