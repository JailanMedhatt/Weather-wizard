package com.example.weatherwizard.data

import android.content.Context
import android.content.SharedPreferences

class SharedPref (context: Context){
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences(FILENAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    companion object {
        private const val FILENAME = "sharedPrefFile"
        private const val LANGGUAGE = "language"
        private const val TEMPUNIT = "TEMPunit"
        private const val WindSpeedUnit = "WindSpeedUnit"
        @Volatile
        private var instance: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            return instance ?: synchronized(this) {
                instance ?: SharedPref(context).also { instance = it }
            }
        }
    }
    fun setLanguage(language: String) {
        editor.putString(LANGGUAGE, language).apply()
        editor.commit()

    }
    fun getLanguage(): String? {
        return sharedPreferences.getString(LANGGUAGE,"en")
    }
fun setTempUnit(tempUnit: String) {
    editor.putString(TEMPUNIT, tempUnit).apply()

}
    fun getTempUnit(): String? {
        return sharedPreferences.getString(TEMPUNIT, "metric")
    }
    fun setWindSpeedUnit(windSpeedUnit: String) {
        editor.putString(WindSpeedUnit, windSpeedUnit).apply()
        editor.commit()
    }
    fun getWindSpeedUnit(): String? {
        return sharedPreferences.getString(WindSpeedUnit, "Meter/Sec")
    }
    fun setLongitudeAndLatitude(longitude: Double, latitude: Double) {
        editor.putFloat("longitude", longitude.toFloat()).apply()
        editor.putFloat("latitude", latitude.toFloat()).apply()
        editor.commit()
    }
    fun getLatitudeAndLongitude():Pair< Double,Double> {
        return Pair(sharedPreferences.getFloat("latitude", 0.0f).toDouble()
            ,sharedPreferences.getFloat("longitude", 0.0f).toDouble())
    }

    fun setGpsSelected(isSelected: Boolean) {
        editor.putBoolean("gpsSelected", isSelected).apply()
        editor.commit()
    }
    fun getGpsSelected(): Boolean {
        return sharedPreferences.getBoolean("gpsSelected", true)
    }
    fun setTheme(theme: String) {
        editor.putString("theme", theme).apply()
        editor.commit()
    }
    fun getTheme(): String? {
        return  sharedPreferences.getString("theme", "dark")
    }



}