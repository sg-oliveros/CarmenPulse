package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.User
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.*

object AuthRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

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
        SupabaseClient.supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logOut() {
        SupabaseClient.supabase.auth.signOut()
    }

    suspend fun sendResetPasswordCode(email: String) {
        SupabaseClient.supabase.auth.resetPasswordForEmail(email)
    }

    suspend fun verifyResetCode(email: String, code: String) {
        SupabaseClient.supabase.auth.verifyEmailOtp(
            type = OtpType.Email.RECOVERY,
            email = email,
            token = code
        )
    }

    suspend fun updatePassword(newPassword: String) {
        val user = SupabaseClient.supabase.auth.currentSessionOrNull()?.user

        // 1. Update password in Supabase Auth
        SupabaseClient.supabase.auth.updateUser {
            password = newPassword
        }

        // 2. Update password_hash in public.users table (per database schema)
        if (user != null) {
            val uid = user.id
            try {
                SupabaseClient.supabase.from("users").update(buildJsonObject {
                    put("password_hash", newPassword)
                }) {
                    filter {
                        eq("auth_user_id", uid)
                    }
                }
            } catch (_: Exception) {
                // Ignore if database RLS or trigger manages password_hash
            }
        }

        // 3. Close the recovery session so user logs in with new password
        SupabaseClient.supabase.auth.signOut()
    }

    suspend fun getCurrentProfile(): User? {
        val authUser = SupabaseClient.supabase.auth.currentSessionOrNull()?.user ?: return null
        val userId = authUser.id

        return try {
            val responseData = SupabaseClient.supabase.from("users")
                .select {
                    filter {
                        eq("auth_user_id", userId)
                    }
                }.data

            val userObj = json.parseToJsonElement(responseData).jsonArray.firstOrNull()?.jsonObject
            if (userObj != null) {
                val uId = userObj["user_id"]?.jsonPrimitive?.longOrNull ?: 1L
                val rId = userObj["role_id"]?.jsonPrimitive?.longOrNull ?: 2L
                val fn = userObj["first_name"]?.jsonPrimitive?.contentOrNull
                val ln = userObj["last_name"]?.jsonPrimitive?.contentOrNull
                val em = userObj["email"]?.jsonPrimitive?.contentOrNull ?: authUser.email
                val cn = userObj["contact_number"]?.jsonPrimitive?.contentOrNull

                User(
                    user_id = uId,
                    role_id = rId,
                    auth_user_id = userId,
                    first_name = fn,
                    last_name = ln,
                    email = em,
                    contact_number = cn
                )
            } else {
                // Insert profile into public.users table so foreign keys work!
                val metaData = authUser.userMetadata
                val metaFirstName = metaData?.get("first_name")?.jsonPrimitive?.contentOrNull
                val metaLastName = metaData?.get("last_name")?.jsonPrimitive?.contentOrNull
                val metaContact = metaData?.get("contact_number")?.jsonPrimitive?.contentOrNull
                val metaRoleId = metaData?.get("role_id")?.jsonPrimitive?.longOrNull ?: 2L

                val insertedData = try {
                    SupabaseClient.supabase.from("users")
                        .insert(buildJsonObject {
                            put("auth_user_id", userId)
                            put("email", authUser.email)
                            put("first_name", metaFirstName)
                            put("last_name", metaLastName)
                            put("contact_number", metaContact)
                            put("role_id", metaRoleId)
                        }) {
                            select()
                        }.data
                } catch (_: Exception) {
                    ""
                }

                val newObj = try {
                    json.parseToJsonElement(insertedData).jsonArray.firstOrNull()?.jsonObject
                } catch (_: Exception) {
                    null
                }

                val newUserId = newObj?.get("user_id")?.jsonPrimitive?.longOrNull ?: 1L

                User(
                    user_id = newUserId,
                    role_id = metaRoleId,
                    auth_user_id = userId,
                    first_name = metaFirstName,
                    last_name = metaLastName,
                    email = authUser.email,
                    contact_number = metaContact
                )
            }
        } catch (_: Exception) {
            val metaData = authUser.userMetadata
            val metaFirstName = metaData?.get("first_name")?.jsonPrimitive?.contentOrNull
            val metaLastName = metaData?.get("last_name")?.jsonPrimitive?.contentOrNull
            val metaRoleId = metaData?.get("role_id")?.jsonPrimitive?.longOrNull ?: 2L

            User(
                user_id = 1L,
                role_id = metaRoleId,
                auth_user_id = userId,
                first_name = metaFirstName,
                last_name = metaLastName,
                email = authUser.email
            )
        }
    }
}
