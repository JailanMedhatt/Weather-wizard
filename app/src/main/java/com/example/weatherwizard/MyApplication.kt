package com.example.weatherwizard

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.util.Locale

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
     //  applySavedLanguage(this)
    }
     fun applySavedLanguage(context: Context) {
        val sharedPreferences = SharedPref.getInstance(context)
        val language = sharedPreferences.getLanguage() ?: "ar"
        Log.i("tag", "applySavedLanguage:$language ")

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createConfigurationContext(config)
            resources.updateConfiguration(config, resources.displayMetrics)
        } else {
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }


}