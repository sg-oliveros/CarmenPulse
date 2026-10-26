package com.example.carmenpulse

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carmenpulse.data.models.AppNotification
import com.example.carmenpulse.data.models.Posts
import com.example.carmenpulse.data.repository.NotificationRepository
import com.example.carmenpulse.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {
    private val _advisories = MutableStateFlow<List<Advisory>>(emptyList())
    val advisories: StateFlow<List<Advisory>> = _advisories.asStateFlow()

    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingNotifications = MutableStateFlow(false)
    val isLoadingNotifications: StateFlow<Boolean> = _isLoadingNotifications.asStateFlow()

    val hasUnread: StateFlow<Boolean> = NotificationRepository.hasUnread

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchAdvisories()
        fetchNotifications()
    }

    fun fetchAdvisories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val posts = PostRepository.fetchPosts()
                _advisories.value = posts.sortedByDescending { it.post_id }.map { it.toAdvisory() }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load advisories"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchNotifications(context: Context? = null) {
        viewModelScope.launch {
            _isLoadingNotifications.value = true
            try {
                val list = NotificationRepository.fetchRecentNotifications(context, 30)
                _notifications.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingNotifications.value = false
            }
        }
    }

    fun markNotificationsAsRead(context: Context) {
        NotificationRepository.markAllAsRead(context, _notifications.value)
    }
}

private fun Posts.toAdvisory(): Advisory {
    return Advisory(
        id = post_id.toString(),
        title = title,
        date = parseAndFormatDate(created_at),
        category = category ?: "Notice",
        snippet = content
    )
}

private fun parseAndFormatDate(rawDateStr: String?): String {
    if (rawDateStr.isNullOrBlank()) return ""
    val pattern = "MMM dd, yyyy • h:mm a"
    val outputFormatter = DateTimeFormatter.ofPattern(pattern)

    return try {
        val parsedOffsetDateTime = try {
            OffsetDateTime.parse(rawDateStr)
        } catch (e: Exception) {
            null
        }

        if (parsedOffsetDateTime != null) {
            val localTime = parsedOffsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
            return localTime.format(outputFormatter)
        }

        val normalized = rawDateStr.replace(" ", "T")

        val parsedLocalDateTime = try {
            LocalDateTime.parse(normalized)
        } catch (e: Exception) {
            null
        }

        if (parsedLocalDateTime != null) {
            return parsedLocalDateTime.format(outputFormatter)
        }

        val parsedZonedDateTime = try {
            ZonedDateTime.parse(rawDateStr)
        } catch (e: Exception) {
            null
        }

        if (parsedZonedDateTime != null) {
            return parsedZonedDateTime.format(outputFormatter)
        }

        rawDateStr
    } catch (e: Exception) {
        rawDateStr
    }
}
