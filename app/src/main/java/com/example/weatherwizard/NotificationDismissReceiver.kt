package com.example.weatherwizard
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer

class NotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        MyWorker.mediaPlayer?.stop()
        MyWorker.mediaPlayer?.release()
        MyWorker.mediaPlayer = null

        // Dismiss the notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1) // Make sure this ID matches the one used in showNotification
    }
}

class NotificationNavigateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MyWorker.mediaPlayer?.stop()
        MyWorker.mediaPlayer?.release()
        MyWorker.mediaPlayer = null
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1) // Replace with your notification ID

        // Now launch MainActivity
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(mainIntent)
    }


}