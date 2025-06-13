package com.example.team.model

// chat api Response 는 meta 정보와 data 로 구성됨
data class ChatApiResponse(
    val meta: ChatMeta,
    val data: List<String>
)

data class ChatMeta(
    val status: Int,
    val message: String
) 