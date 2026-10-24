package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ServiceInterest(
    val interest_id: Long? = null,
    val service_id: Long,
    val user_id: Long,
    val created_at: String? = null
)
