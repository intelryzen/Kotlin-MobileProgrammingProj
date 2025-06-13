package com.example.team.model

data class VocabularyApiResponse(
    val meta: VocabularyMeta,
    val data: List<VocabularyItem>
)

data class VocabularyMeta(
    val status: Int,
    val message: String
)