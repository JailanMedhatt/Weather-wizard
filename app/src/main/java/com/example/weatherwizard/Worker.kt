package com.example.weatherwizard
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherwizard.Network.RemoteDataSource
import com.example.weatherwizard.Network.RetrofitHelper
import com.example.weatherwizard.data.Repository
import com.example.weatherwizard.data.SharedPref
import com.example.weatherwizard.data.database.AppDb
import com.example.weatherwizard.data.database.LocalDataSource
import kotlinx.coroutines.flow.first

class MyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        var mediaPlayer: MediaPlayer? = null
    }
    private val repo = Repository.getInstance(
        RemoteDataSource(RetrofitHelper.retrofitInstance),
        LocalDataSource(
            AppDb.getInstance(context).getDao(),
            AppDb.getInstance(context).getAlertDao(),AppDb.getInstance(context).getHomeDao()
        )
    )

    override suspend fun doWork(): Result {
        return try {
            // Fetch weather data
            val sharedPref = SharedPref.getInstance(applicationContext)
            val pair = sharedPref.getLatitudeAndLongitude()
            val language = sharedPref.getLanguage()?:"en"
            val unit = sharedPref.getTempUnit()?:"metric"
            val weatherResponse = repo.getCurrentWeather(pair.first, pair.second, language, unit).first()

            // Extract data (Customize based on your API response)
            val temperature = weatherResponse.main?.temp ?: 0.0
            val description = weatherResponse.weather?.firstOrNull()?.description ?: "No description"
            Log.i("TAGG", "showNotification: work")
            // Show notification
            showNotification("Weather Update", "Temp: $temperature°C, $description")

            Result.success()
        } catch (e: Exception) {
            Log.i("TAGG", "showNotification: error ${e.message}")
            e.printStackTrace()
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "weather_alert_channel"

        // Create Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH

            ).apply {
                description = "Weather alert notifications"
                setSound(null, null)
            }
            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }
        mediaPlayer?.release() // Release any existing player
        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.ringtone)
        mediaPlayer?.isLooping = true // Loop the sound
        mediaPlayer?.start()
        val dismissIntent = Intent(applicationContext, NotificationDismissReceiver::class.java)
        val dismissPendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val mainIntent = Intent(applicationContext, NotificationNavigateReceiver::class.java)
        val mainPendingIntent = PendingIntent.getBroadcast(
            applicationContext, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Build and Show Notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.cloudy)  // Replace with your actual drawable icon
            .setContentTitle(title)
            .setContentText(message)
            .addAction(R.drawable.ic_launcher_foreground, "Dismiss", dismissPendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "View Details", mainPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            Log.i("TAGG", "Notification permission zz")
            return
        }
        NotificationManagerCompat.from(applicationContext).notify(1, notification)
    }
}