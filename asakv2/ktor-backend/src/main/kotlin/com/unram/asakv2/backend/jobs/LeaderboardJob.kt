package com.unram.asakv2.backend.jobs

import kotlinx.coroutines.*
import kotlin.time.Duration.Companion.hours

@OptIn(DelicateCoroutinesApi::class)
fun initCronJobs() {
    GlobalScope.launch(Dispatchers.IO) {
        while(isActive) {
            println("Cron job tick for leaderboard")
            delay(1.hours)
        }
    }
}
