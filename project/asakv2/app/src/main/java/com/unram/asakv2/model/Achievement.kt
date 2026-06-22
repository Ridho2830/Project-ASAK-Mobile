package com.unram.asakv2.model

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val category: String = "",      
    val requirement: Int = 0,       
    val requirementType: String = "" 
)
