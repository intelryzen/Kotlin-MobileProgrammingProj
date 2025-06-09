package com.example.team.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab")
data class VocabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val partOfSpeech: String,      // 예: noun, verb 등
    val meaning: String,           // 한국어 뜻
    val example: String,
    val createdDate: String        // 예: "2025-06-05"
)