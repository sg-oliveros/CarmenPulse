package com.example.carmenpulse.data.models

import kotlinx.serialization.Serializable

@Serializable
data class MedicalServices(
    val service_id: Long,
    val created_by_user_id: Long,
    val service_name: String,
    val description: String? = null,
    val schedule_date: String,
    val location_venue: String,
    val requirements: String? = null,
    val status: String? = null,
    val created_at: String? = null

)