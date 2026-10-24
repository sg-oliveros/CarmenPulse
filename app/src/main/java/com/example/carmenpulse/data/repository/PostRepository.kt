package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.Posts
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object PostRepository {
    suspend fun fetchPosts(): List<Posts> {
        return SupabaseClient.supabase.from("posts")
            .select {
                order("post_id", Order.DESCENDING)
            }
            .decodeList<Posts>()
    }
}
