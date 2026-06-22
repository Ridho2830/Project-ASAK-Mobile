package com.unram.asakv2.model

data class AksaraHuruf(
    val id: String = "",
    val name: String = "",          
    val imageUrl: String = "",      
    val localImagePath: String = "",
    val audioUrl: String = "",      
    val localAudioPath: String = "",
    val levelRequired: Int = 1,     
    val orderIndex: Int = 0         
)
