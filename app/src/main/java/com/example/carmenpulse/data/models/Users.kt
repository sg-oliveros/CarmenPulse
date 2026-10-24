package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val user_id: Long,
    val role_id: Long? = null,
    val auth_user_id: String? = null,
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val contact_number: String? = null,
    val created_at: String? = null
)
