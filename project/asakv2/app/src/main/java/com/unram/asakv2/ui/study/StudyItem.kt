package com.unram.asakv2.ui.study

enum class StudyType {
    HEADER_HURUF,
    HURUF,
    HEADER_WISATA,
    WISATA,
    HEADER_BUDAYA,
    BUDAYA
}

data class StudyItem(
    val id: String,
    val name: String,
    val type: StudyType,
    val levelRequired: Int = 1,
    val achievementKey: String = ""
)