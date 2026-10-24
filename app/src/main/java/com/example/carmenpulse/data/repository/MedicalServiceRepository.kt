package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.MedicalServices
import com.example.carmenpulse.data.models.ServiceInterest
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

    suspend fun fetchUserInterestedServiceIds(userId: Long): Set<Long> {
        return try {
            SupabaseClient.supabase.from("service_interests")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<ServiceInterest>()
                .map { it.service_id }
                .toSet()
        } catch (_: Exception) {
            emptySet()
        }
    }

    suspend fun fetchServiceInterestsCounts(): Map<Long, Int> {
        return try {
            val allInterests = SupabaseClient.supabase.from("service_interests")
                .select()
                .decodeList<ServiceInterest>()
            allInterests.groupingBy { it.service_id }.eachCount()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    suspend fun toggleServiceInterest(serviceId: Long, userId: Long, isInterested: Boolean) {
        if (isInterested) {
            SupabaseClient.supabase.from("service_interests")
                .insert(ServiceInterest(service_id = serviceId, user_id = userId))
        } else {
            SupabaseClient.supabase.from("service_interests")
                .delete {
                    filter {
                        eq("service_id", serviceId)
                        eq("user_id", userId)
                    }
                }
        }
    }
}
