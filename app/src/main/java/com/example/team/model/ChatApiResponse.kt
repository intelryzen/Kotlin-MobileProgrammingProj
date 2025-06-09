package com.example.team.model

data class ChatApiResponse(
    val meta: ChatMeta,
    val data: List<String>
)

data class ChatMeta(
    val status: Int,
    val message: String
) 