package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CommentAuthor(
    val first_name: String,
    val last_name: String,
    val role_id: Long
)

@Serializable
data class  Comment(
    val comment_id: Long,
    val post_id: Long,
    val user_id: Long,
    val content: String,
    val created_at: String? = null,
    val users: CommentAuthor? = null
)