package com.example.carmenpulse.util

import android.content.Context
import android.util.Log
import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.models.Posts
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

object RealtimeNotificationManager {

    private const val TAG = "RealtimeNotify"
    private var listeningJob: Job? = null

    fun startListening(context: Context, scope: CoroutineScope) {
        if (listeningJob?.isActive == true) return

        listeningJob = scope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "Connecting to Supabase Realtime...")
                SupabaseClient.supabase.realtime.connect()

                val channel = SupabaseClient.supabase.channel("app_realtime_channel")

                // 1. Listen for new Posts (Health Advisories)
                val postChangeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "posts"
                }
                postChangeFlow.onEach { action ->
                    try {
                        val newPost = action.decodeRecord<Posts>()
                        Log.d(TAG, "Received new post: ${newPost.title}")
                        NotificationHelper.showNotification(
                            context = context,
                            channelId = NotificationHelper.CHANNEL_ADVISORIES_ID,
                            title = "📢 Advisory: ${newPost.title}",
                            message = newPost.content,
                            notificationId = newPost.post_id.toInt()
                        )
                        com.example.carmenpulse.data.repository.NotificationRepository.markUnread()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decoding post in realtime", e)
                    }
                }.launchIn(this)

                // 2. Listen for new Medical Services
                val serviceChangeFlow = channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "medical_services"
                }
                serviceChangeFlow.onEach { action ->
                    try {
                        val newService = action.decodeRecord<MedicalServices>()
                        Log.d(TAG, "Received new medical service: ${newService.service_name}")
                        NotificationHelper.showNotification(
                            context = context,
                            channelId = NotificationHelper.CHANNEL_ADVISORIES_ID,
                            title = "🏥 New Service: ${newService.service_name}",
                            message = newService.description ?: "Location: ${newService.location_venue}",
                            notificationId = newService.service_id.toInt()
                        )
                        com.example.carmenpulse.data.repository.NotificationRepository.markUnread()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error decoding medical service in realtime", e)
                    }
                }.launchIn(this)

                channel.subscribe()
                Log.d(TAG, "Subscribed to posts and medical_services realtime channels")
            } catch (e: Exception) {
                Log.e(TAG, "Error connecting or subscribing to Realtime", e)
            }
        }
    }

    fun stopListening(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            try {
                SupabaseClient.supabase.realtime.disconnect()
            } catch (_: Exception) {
            }
        }
        listeningJob?.cancel()
        listeningJob = null
    }
}
