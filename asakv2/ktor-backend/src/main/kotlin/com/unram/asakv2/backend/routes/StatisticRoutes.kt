package com.unram.asakv2.backend.routes

import com.unram.asakv2.backend.plugins.db
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.statisticRoutes() {
    get("/users/{user_id}/statistics") {
        try {
            val userId = call.parameters["user_id"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "user_id is required"))
                return@get
            }

            val userDoc = db.collection("users").document(userId).get().get()
            val userData = if (userDoc.exists()) userDoc.data else mapOf("total_xp" to 0L, "current_streak" to 0L, "highest_streak" to 0L)

            val sessionsSnapshot = db.collection("quiz_sessions").whereEqualTo("user_id", userId).get().get()
            val totalSessions = sessionsSnapshot.size()
            
            var totalAccuracy = 0.0
            val xpByDay = mutableMapOf<String, Long>()
            val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            days.forEach { xpByDay[it] = 0L }

            sessionsSnapshot.documents.forEach { doc ->
                val accStr = doc.data["accuracy"]?.toString() ?: "0.0"
                totalAccuracy += accStr.toDoubleOrNull() ?: 0.0
                
                val xpStr = doc.data["xpEarned"]?.toString() ?: "0"
                val xp = xpStr.toLongOrNull() ?: 0L

                val startTimeStr = doc.data["startTime"]?.toString() ?: "0"
                val startTime = startTimeStr.toLongOrNull() ?: 0L

                if (startTime > 0) {
                    val date = java.time.Instant.ofEpochMilli(startTime).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    val dayName = date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.ENGLISH)
                    xpByDay[dayName] = (xpByDay[dayName] ?: 0L) + xp
                }
            }
            
            val avgAccuracy = if (totalSessions > 0) totalAccuracy / totalSessions else 0.0
            val weeklyChart = days.map { mapOf("day" to it, "xp" to (xpByDay[it] ?: 0L)) }

            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "success",
                "data" to mapOf(
                    "total_xp" to (userData?.get("total_xp") ?: 0L),
                    "current_streak" to (userData?.get("current_streak") ?: 0L),
                    "highest_streak" to (userData?.get("highest_streak") ?: 0L),
                    "total_sessions" to totalSessions,
                    "avg_accuracy" to avgAccuracy,
                    "weekly_xp_chart" to weeklyChart
                )
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }
}
