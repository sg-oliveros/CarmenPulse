package com.example.carmenpulse.data.repository

import android.content.Context
import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.AppNotification
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.models.NotificationType
import com.example.carmenpulse.data.models.Posts
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object NotificationRepository {

    private const val PREFS_NAME = "notification_read_prefs"
    private const val KEY_READ_IDS = "read_notification_ids"

    private val _hasUnread = MutableStateFlow(false)
    val hasUnread: StateFlow<Boolean> = _hasUnread.asStateFlow()

    suspend fun fetchRecentNotifications(context: Context? = null, limit: Int = 30): List<AppNotification> {
        val notifications = mutableListOf<AppNotification>()

        try {
            val posts = SupabaseClient.supabase.from("posts")
                .select {
                    order("post_id", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<Posts>()

            posts.forEach { post ->
                notifications.add(
                    AppNotification(
                        id = "post_${post.post_id}",
                        title = "📢 Advisory: ${post.title}",
                        message = post.content,
                        dateStr = parseAndFormatDate(post.created_at),
                        type = NotificationType.POST,
                        rawTimestamp = post.created_at ?: ""
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val services = SupabaseClient.supabase.from("medical_services")
                .select {
                    order("service_id", Order.DESCENDING)
                    limit(limit.toLong())
                }
                .decodeList<MedicalServices>()

            services.forEach { service ->
                notifications.add(
                    AppNotification(
                        id = "service_${service.service_id}",
                        title = "🏥 Medical Service: ${service.service_name}",
                        message = service.description ?: "Venue: ${service.location_venue} on ${service.schedule_date}",
                        dateStr = parseAndFormatDate(service.created_at ?: service.schedule_date),
                        type = NotificationType.MEDICAL_SERVICE,
                        rawTimestamp = service.created_at ?: service.schedule_date ?: ""
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Sort descending by raw timestamp/ID and cap at limit (30)
        val sortedList = notifications
            .sortedByDescending { it.rawTimestamp }
            .take(limit)

        if (context != null) {
            checkUnreadStatus(context, sortedList)
        }

        return sortedList
    }

    fun checkUnreadStatus(context: Context, notifications: List<AppNotification>) {
        if (notifications.isEmpty()) {
            _hasUnread.value = false
            return
        }
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val readIds = prefs.getStringSet(KEY_READ_IDS, emptySet()) ?: emptySet()

        val unreadExists = notifications.any { !readIds.contains(it.id) }
        _hasUnread.value = unreadExists
    }

    fun markAllAsRead(context: Context, notifications: List<AppNotification>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val existingReadIds = prefs.getStringSet(KEY_READ_IDS, emptySet()) ?: emptySet()

        val updatedReadIds = existingReadIds + notifications.map { it.id }.toSet()
        prefs.edit().putStringSet(KEY_READ_IDS, updatedReadIds).apply()

        _hasUnread.value = false
    }

    fun markUnread() {
        _hasUnread.value = true
    }

    private fun parseAndFormatDate(rawDateStr: String?): String {
        if (rawDateStr.isNullOrBlank()) return ""
        val outputFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • h:mm a")

        return try {
            val parsedOffset = try { OffsetDateTime.parse(rawDateStr) } catch (_: Exception) { null }
            if (parsedOffset != null) {
                return parsedOffset.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime().format(outputFormatter)
            }
            val normalized = rawDateStr.replace(" ", "T")
            val parsedLocal = try { LocalDateTime.parse(normalized) } catch (_: Exception) { null }
            if (parsedLocal != null) {
                return parsedLocal.format(outputFormatter)
            }
            val parsedZoned = try { ZonedDateTime.parse(rawDateStr) } catch (_: Exception) { null }
            if (parsedZoned != null) {
                return parsedZoned.format(outputFormatter)
            }
            rawDateStr
        } catch (_: Exception) {
            rawDateStr
        }
    }
}
