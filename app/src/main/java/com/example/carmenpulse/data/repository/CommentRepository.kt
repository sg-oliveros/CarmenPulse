package com.example.carmenpulse.data.repository

import com.example.carmenpulse.data.SupabaseClient
import com.example.carmenpulse.data.models.Comment
import com.example.carmenpulse.data.models.CommentAuthor
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.serialization.json.*

object CommentRepository {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    suspend fun fetchComments(serviceId: Long, limit: Long = 100): List<Comment> {
        val currentProfile = try { AuthRepository.getCurrentProfile() } catch (_: Exception) { null }

        val responseData = try {
            SupabaseClient.supabase.from("comments")
                .select(columns = Columns.raw("*, users!comments_user_id_fkey(role_id)")) {
                    filter { eq("service_id", serviceId) }
                    order("created_at", Order.ASCENDING)
                    limit(limit)
                }.data
        } catch (_: Exception) {
            try {
                SupabaseClient.supabase.from("comments")
                    .select(columns = Columns.raw("*, users!user_id(role_id)")) {
                        filter { eq("service_id", serviceId) }
                        order("created_at", Order.ASCENDING)
                        limit(limit)
                    }.data
            } catch (_: Exception) {
                try {
                    SupabaseClient.supabase.from("comments")
                        .select(columns = Columns.raw("*, users(role_id)")) {
                            filter { eq("service_id", serviceId) }
                            order("created_at", Order.ASCENDING)
                            limit(limit)
                        }.data
                } catch (_: Exception) {
                    try {
                        SupabaseClient.supabase.from("comments")
                            .select {
                                filter { eq("service_id", serviceId) }
                                order("created_at", Order.ASCENDING)
                                limit(limit)
                            }.data
                    } catch (_: Exception) {
                        ""
                    }
                }
            }
        }

        if (responseData.isBlank()) return emptyList()

        val jsonArray = try {
            json.parseToJsonElement(responseData).jsonArray
        } catch (_: Exception) {
            JsonArray(emptyList())
        }

        val commentsList = jsonArray.mapNotNull { element ->
            try {
                val obj = element.jsonObject
                val commentId = obj["comment_id"]?.jsonPrimitive?.long ?: return@mapNotNull null
                val serviceIdVal = obj["service_id"]?.jsonPrimitive?.long ?: serviceId
                val userIdVal = obj["user_id"]?.jsonPrimitive?.long ?: return@mapNotNull null
                val contentVal = obj["content"]?.jsonPrimitive?.content ?: ""
                val createdAtVal = obj["created_at"]?.jsonPrimitive?.contentOrNull

                var roleId: Long? = null

                if (userIdVal == 16L || userIdVal == 30L || userIdVal == 1L) {
                    roleId = 1L
                }

                val usersElement = obj["users"] ?: obj["users!user_id"] ?: obj["users!comments_user_id_fkey"]
                if (usersElement is JsonObject) {
                    roleId = usersElement["role_id"]?.jsonPrimitive?.longOrNull ?: roleId
                } else if (usersElement is JsonArray && usersElement.isNotEmpty()) {
                    roleId = usersElement.first().jsonObject["role_id"]?.jsonPrimitive?.longOrNull ?: roleId
                }

                if (roleId == null && currentProfile != null && currentProfile.user_id == userIdVal) {
                    roleId = currentProfile.role_id
                }

                Comment(
                    comment_id = commentId,
                    service_id = serviceIdVal,
                    user_id = userIdVal,
                    content = contentVal,
                    created_at = createdAtVal,
                    users = CommentAuthor(role_id = roleId)
                )
            } catch (_: Exception) {
                null
            }
        }

        if (commentsList.isEmpty()) return emptyList()

        val missingRoleUserIds = commentsList
            .filter { it.users?.role_id == null }
            .map { it.user_id }
            .distinct()

        if (missingRoleUserIds.isNotEmpty()) {
            val userRoleMap = mutableMapOf<Long, Long>()

            try {
                val usersData = SupabaseClient.supabase.from("users")
                    .select(columns = Columns.raw("user_id, role_id")) {
                        filter {
                            isIn("user_id", missingRoleUserIds)
                        }
                    }.data
                val uArray = json.parseToJsonElement(usersData).jsonArray
                for (uElem in uArray) {
                    val uObj = uElem.jsonObject
                    val uId = uObj["user_id"]?.jsonPrimitive?.longOrNull ?: continue
                    val rId = uObj["role_id"]?.jsonPrimitive?.longOrNull ?: continue
                    userRoleMap[uId] = rId
                }
            } catch (_: Exception) {
                for (uid in missingRoleUserIds) {
                    try {
                        val userData = SupabaseClient.supabase.from("users")
                            .select(columns = Columns.raw("user_id, role_id")) {
                                filter { eq("user_id", uid) }
                            }.data
                        val uObj = json.parseToJsonElement(userData).jsonArray.firstOrNull()?.jsonObject
                        val rId = uObj?.get("role_id")?.jsonPrimitive?.longOrNull
                        if (rId != null) {
                            userRoleMap[uid] = rId
                        }
                    } catch (_: Exception) {}
                }
            }

            return commentsList.map { comment ->
                if (comment.users?.role_id == null) {
                    val fetchedRoleId = userRoleMap[comment.user_id]
                        ?: if (currentProfile?.user_id == comment.user_id) currentProfile.role_id
                        else if (comment.user_id == 16L || comment.user_id == 30L || comment.user_id == 1L) 1L
                        else null
                    comment.copy(users = CommentAuthor(role_id = fetchedRoleId))
                } else comment
            }
        }

        return commentsList
    }

    suspend fun postComment(serviceId: Long, userId: Long, content: String) {
        SupabaseClient.supabase.from("comments").insert(
            buildJsonObject {
                put("service_id", serviceId)
                put("user_id", userId)
                put("content", content)
            }
        )
    }
}
