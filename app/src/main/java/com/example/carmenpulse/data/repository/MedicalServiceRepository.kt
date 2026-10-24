package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.MedicalServices
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object MedicalServiceRepository {
suspend fun fetchMedicalServices(): List<MedicalServices> {
    return SupabaseClient.supabase.from("medical_services")
        .select {
            order("schedule_date", Order.ASCENDING)
        }
        .decodeList<MedicalServices>()
}
}
