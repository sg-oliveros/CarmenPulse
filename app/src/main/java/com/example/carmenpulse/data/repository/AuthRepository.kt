package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.User
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object AuthRepository {

    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        contactNumber: String
    ) {
        SupabaseClient.supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password

            data = buildJsonObject {
                put("first_name", firstName)
                put("last_name", lastName)
                put("contact_number", contactNumber)
                put("role_id", 2)
            }
        }
    }

    suspend fun logIn(email: String, password: String) {
        SupabaseClient.supabase.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logOut() {
        SupabaseClient.supabase.auth.signOut()
    }

    suspend fun getCurrentProfile(): User? {
        val userId = SupabaseClient.supabase.auth.currentSessionOrNull()?.user?.id ?: return null
        return SupabaseClient.supabase.from("users")
            .select {
                filter {
                    eq("auth_user_id", userId)
                }
            }
            .decodeSingleOrNull<User>()
    }
}