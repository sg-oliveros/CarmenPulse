package com.example.carmenpulse.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.models.Posts
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

class NotificationBackgroundWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = appContext.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
            val lastSeenPostId = prefs.getLong("last_seen_post_id", 0L)
            val lastSeenServiceId = prefs.getLong("last_seen_service_id", 0L)

            // 1. Check latest posts (advisories)
            val latestPosts = SupabaseClient.supabase.from("posts")
                .select {
                    order("post_id", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<Posts>()

            if (latestPosts.isNotEmpty()) {
                val latestPost = latestPosts.first()
                if (lastSeenPostId != 0L && latestPost.post_id > lastSeenPostId) {
                    NotificationHelper.showNotification(
                        context = appContext,
                        channelId = NotificationHelper.CHANNEL_ADVISORIES_ID,
                        title = "📢 Advisory: ${latestPost.title}",
                        message = latestPost.content,
                        notificationId = latestPost.post_id.toInt()
                    )
                }
                prefs.edit().putLong("last_seen_post_id", latestPost.post_id).apply()
            }

            // 2. Check latest medical services
            val latestServices = SupabaseClient.supabase.from("medical_services")
                .select {
                    order("service_id", Order.DESCENDING)
                    limit(1)
                }
                .decodeList<MedicalServices>()

            if (latestServices.isNotEmpty()) {
                val latestService = latestServices.first()
                if (lastSeenServiceId != 0L && latestService.service_id > lastSeenServiceId) {
                    NotificationHelper.showNotification(
                        context = appContext,
                        channelId = NotificationHelper.CHANNEL_ADVISORIES_ID,
                        title = "🏥 New Service: ${latestService.service_name}",
                        message = latestService.description ?: "Location: ${latestService.location_venue}",
                        notificationId = latestService.service_id.toInt()
                    )
                }
                prefs.edit().putLong("last_seen_service_id", latestService.service_id).apply()
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
