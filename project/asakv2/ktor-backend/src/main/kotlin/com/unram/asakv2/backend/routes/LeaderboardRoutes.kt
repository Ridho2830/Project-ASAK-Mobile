package com.unram.asakv2.backend.routes

import com.unram.asakv2.backend.plugins.db
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

fun Route.leaderboardRoutes() {
    get("/leaderboard/weekly") {
        try {
            val league = call.request.queryParameters["league"] ?: "Bronze"
            
            val now = LocalDateTime.now()
            val weekId = "2023-W41" 

            val leaderboardRef = db.collection("leaderboard_weekly").document(weekId)
            val rankingsSnapshot = leaderboardRef.collection("rankings")
                .whereEqualTo("league_name", league)
                .orderBy("rank_position")
                .get()
                .get()

            val rankings = rankingsSnapshot.documents.map { doc ->
                val data = doc.data
                data["user_id"] = doc.id
                data
            }

            val nextSunday = now.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
                .withHour(23).withMinute(59).withSecond(59)
            
            val resetInSeconds = nextSunday.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC)

            call.respond(HttpStatusCode.OK, mapOf(
                "status" to "success",
                "data" to mapOf(
                    "league_name" to league,
                    "week_id" to weekId,
                    "reset_in_seconds" to resetInSeconds,
                    "rankings" to rankings
                )
            ))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }
}
