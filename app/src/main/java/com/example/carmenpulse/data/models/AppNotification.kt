package com.example.carmenpulse.data.models

enum class NotificationType {
    POST,
    MEDICAL_SERVICE
}

data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val dateStr: String,
    val type: NotificationType,
    val rawTimestamp: String
)
