package com.unram.asakv2.backend

import com.unram.asakv2.backend.plugins.configureFirebase
import com.unram.asakv2.backend.plugins.configureRouting
import com.unram.asakv2.backend.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureFirebase()
    configureRouting()
}
