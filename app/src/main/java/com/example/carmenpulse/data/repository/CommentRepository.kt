package com.example.carmenpulse.data.repository

import androidx.compose.foundation.layout.Column
import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.Comment
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.result.PostgrestResult
import kotlinx.coroutines.selects.select
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object CommentRepository {

    suspend fun fetchComments(postId: Long): List<Comment> {
        return SupabaseClient.supabase.from("comments")
            .select(columns = Columns.raw("*, users(first_name, last_name, role_id)")) {
                filter { eq("post_id", postId) }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<Comment>()
    }

    suspend fun postComment(postId: Long, userId: Long, content: String) {
        SupabaseClient.supabase.from("comments").insert(
            buildJsonObject {
                put("post_id", postId)
                put("user_id", userId)
                put("content", content)
            }
        )
    }
}
