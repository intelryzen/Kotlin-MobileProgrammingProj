package com.example.team.model

data class DiaryApiResponse(
    val meta: DiaryMeta,
    val data: List<String>
)

data class DiaryMeta(
    val status: Int,
    val message: String
)
