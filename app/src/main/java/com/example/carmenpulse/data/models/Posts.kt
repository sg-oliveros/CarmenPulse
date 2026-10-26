package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    val post_id: Long = 0L,
    val user_id: Long = 0L,
    val title: String = "",
    val content: String = "",
    val category: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
