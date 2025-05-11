package com.example.workouttracker.ui.managers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.workouttracker.R
import com.example.workouttracker.ui.MainActivity

/** Notification manager to send notifications to the user */
object CustomNotificationManager {
    private var channelId = "timer_notification_channel"

    /** Send a notification when the timer finishes
     * @param context the context
     * @param titleId the notification title id
     * @param messageId the message id
     * */
    fun sendNotification(context: Context, titleId: Int, messageId: Int) {
        val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0 and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notification", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Create an Intent to open the app when the notification is clicked
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Create the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(titleId))
            .setContentText(context.getString(messageId))
            .setSmallIcon(R.drawable.icon_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Send the notification
        notificationManager.notify(1, notification)
    }
}