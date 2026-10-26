package com.example.carmenpulse.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.carmenpulse.MainActivity
import com.example.carmenpulse.R

object NotificationHelper {

    const val CHANNEL_ADVISORIES_ID = "channel_advisories"
    const val CHANNEL_SERVICES_ID = "channel_services"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val advisoriesChannel = NotificationChannel(
                CHANNEL_ADVISORIES_ID,
                "Community Advisories",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important community health alerts and advisories"
                enableVibration(true)
            }

            val servicesChannel = NotificationChannel(
                CHANNEL_SERVICES_ID,
                "Medical Services",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Updates on community medical services and health center schedules"
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Delete legacy forum channel
            try {
                notificationManager.deleteNotificationChannel("channel_forum")
            } catch (_: Exception) {}

            notificationManager.createNotificationChannel(advisoriesChannel)
            notificationManager.createNotificationChannel(servicesChannel)
        }
    }

    fun hasPostNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        message: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        if (!hasPostNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(
                if (channelId == CHANNEL_ADVISORIES_ID) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Handled if permission is revoked unexpectedly
        }
    }
}
