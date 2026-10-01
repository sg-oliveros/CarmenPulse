package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.Posts
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

object PostRepository {
    suspend fun fetchPosts(): List<Posts> {
        return SupabaseClient.supabase.postgrest["posts"]
            .select {
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Posts>()
    }
}
