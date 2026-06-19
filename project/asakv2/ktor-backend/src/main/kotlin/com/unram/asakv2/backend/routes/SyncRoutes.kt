package com.unram.asakv2.backend.routes

import com.unram.asakv2.backend.plugins.db
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    val user_id: String,
    val quiz_sessions: List<Map<String, String>> = emptyList(),
    val huruf_progress: List<Map<String, String>> = emptyList()
)

fun Route.syncRoutes() {
    post("/sync") {
        try {
            val request = call.receive<SyncRequest>()
            if (request.user_id.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("status" to "error", "message" to "user_id is required"))
                return@post
            }

            val batch = db.batch()

            request.quiz_sessions.forEach { session ->
                val sessionId = session["id"]
                val sessionRef = if (sessionId != null) {
                    db.collection("quiz_sessions").document(sessionId)
                } else {
                    db.collection("quiz_sessions").document()
                }
                val data = session.toMutableMap()
                data["user_id"] = request.user_id
                batch.set(sessionRef, data)
            }

            request.huruf_progress.forEach { progress ->
                val hurufId = progress["huruf_id"]
                if (hurufId != null) {
                    val docId = "${request.user_id}_${hurufId}"
                    val progressRef = db.collection("user_huruf_progress").document(docId)
                    val data = progress.toMutableMap()
                    data["user_id"] = request.user_id
                    batch.set(progressRef, data)
                }
            }

            batch.commit().get()

            call.respond(HttpStatusCode.OK, mapOf("status" to "success", "message" to "Data successfully synced"))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }
}
