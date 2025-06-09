package com.example.team.model

data class VocabularyApiResponse(
    val meta: VocabularyMeta,
    val data: List<VocabularyItem>
)

data class VocabularyMeta(
    val status: Int,
    val message: String
)

data class VocabularyItem(
    val word: String,
    val partOfSpeech: String,
    val meaning: String,
    val exampleSentence: String
) 