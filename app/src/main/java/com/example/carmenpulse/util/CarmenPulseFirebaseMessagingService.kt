package com.example.carmenpulse.util

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CarmenPulseFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "CareConnect Notification"

        val message = remoteMessage.notification?.body
            ?: remoteMessage.data["message"]
            ?: remoteMessage.data["body"]
            ?: ""

        val channelId = when (remoteMessage.data["type"]) {
            "service", "medical_service" -> NotificationHelper.CHANNEL_SERVICES_ID
            else -> NotificationHelper.CHANNEL_ADVISORIES_ID
        }

        NotificationHelper.showNotification(
            context = applicationContext,
            channelId = channelId,
            title = title,
            message = message
        )

        com.example.carmenpulse.data.repository.NotificationRepository.markUnread()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMToken", "New FCM token: $token")
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("all_notifications")
        } catch (_: Throwable) {}
    }
}
