package com.unram.asakv2.backend.plugins

import com.unram.asakv2.backend.routes.statisticRoutes
import com.unram.asakv2.backend.routes.syncRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("ASAK v2 Ktor Backend is running!")
        }
        
        route("/api/v1") {
            syncRoutes()
            statisticRoutes()
        }
    }
}
