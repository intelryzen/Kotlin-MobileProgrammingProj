package com.example.team.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocab")
data class VocabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val partOfSpeech: String,
    val meaning: String, // 추가
    val example: String,
    val createdDate: String
)
