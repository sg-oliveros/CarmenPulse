package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: Long,
    val role_id: Long,
    val auth_user_id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val contact_number: String,
    val created_at: String,
)