package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    val post_id: Long,
    val user_id: Long,
    val title: String,
    val content: String,
    val category: String,
    val created_at: String,
    val updated_at: String
)
