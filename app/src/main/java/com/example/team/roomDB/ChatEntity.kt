package com.example.team.roomDB

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "chat",
    foreignKeys = [ForeignKey(
        entity = DiaryEntity::class,
        parentColumns = ["id"],
        childColumns = ["diaryId"],
        onDelete = ForeignKey.SET_NULL
    )]
)

data class ChatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diaryId: Int?,
    val createdDate: String,      // 예: "2025-06-05"
    val isQuestion: Boolean,      // true = 질문, false = 답변
    val content: String
)
