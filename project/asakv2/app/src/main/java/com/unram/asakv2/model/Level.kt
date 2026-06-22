package com.unram.asakv2.model

data class Level(
    val level: Int = 1,
    val title: String = "",
    val description: String = "",
    val xpRequired: Int = 0,       
    val hurufUnlocked: List<String> = emptyList() 
)
