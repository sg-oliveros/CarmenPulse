package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.MedicalServices
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

object MedicalServiceRepository {
    suspend fun fetchMedicalServices(
        statusFilter: String? = null,
        searchQuery: String? = null,
        limit: Long = 10,
        offset: Long = 0
    ): List<MedicalServices> {
        return SupabaseClient.supabase.from("medical_services")
            .select {
                filter {
                    if (!statusFilter.isNullOrBlank() && !statusFilter.equals("All", ignoreCase = true)) {
                        eq("status", statusFilter)
                    }
                    if (!searchQuery.isNullOrBlank()) {
                        or {
                            ilike("service_name", "%$searchQuery%")
                            ilike("description", "%$searchQuery%")
                            ilike("location_venue", "%$searchQuery%")
                        }
                    }
                }
                order("service_id", Order.DESCENDING)
                range(offset, offset + limit - 1)
            }
            .decodeList<MedicalServices>()
    }
}
