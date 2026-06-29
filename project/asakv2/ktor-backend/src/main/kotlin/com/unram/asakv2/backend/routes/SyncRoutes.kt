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

            application.log.info("=== MENERIMA SYNC DATA ===")
            application.log.info("User ID: ${request.user_id}")
            application.log.info("Jumlah Sesi Kuis: ${request.quiz_sessions.size} item")
            application.log.info("Jumlah Progres Huruf: ${request.huruf_progress.size} item")

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
                batch.set(sessionRef, data as Map<String, Any>)
            }

            request.huruf_progress.forEach { progress ->
                val hurufId = progress["huruf_id"]
                if (hurufId != null) {
                    val docId = "${request.user_id}_${hurufId}"
                    val progressRef = db.collection("user_huruf_progress").document(docId)
                    val data = progress.toMutableMap()
                    data["user_id"] = request.user_id
                    batch.set(progressRef, data as Map<String, Any>)
                }
            }

            batch.commit().get()
            application.log.info("Status: SUKSES sinkronisasi data ke Firestore Cloud.")

            call.respond(HttpStatusCode.OK, mapOf("status" to "success", "message" to "Data successfully synced"))
        } catch (e: Exception) {
            application.log.error("Status: GAGAL sinkronisasi data. Detail: ${e.message}", e)
            call.respond(HttpStatusCode.InternalServerError, mapOf("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }
}
