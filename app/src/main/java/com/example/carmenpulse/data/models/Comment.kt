package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CommentAuthor(
    val first_name: String? = null,
    val last_name: String? = null,
    val role_id: Long? = null
)

@Serializable
data class Comment(
    val comment_id: Long,
    val service_id: Long,
    val user_id: Long,
    val content: String,
    val created_at: String? = null,
    val users: CommentAuthor? = null
)
