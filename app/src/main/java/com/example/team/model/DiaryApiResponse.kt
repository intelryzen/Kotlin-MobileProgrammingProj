package com.example.team.model

data class DiaryApiResponse(
    val meta: Meta,
    val data: List<String>
)

data class Meta(
    val status: Int,
    val message: String
)
