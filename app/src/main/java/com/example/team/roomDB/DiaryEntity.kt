package com.example.team.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val createdDate: String,        // 예: "2025-06-05"
    val title: String,
    val content: String,
    val correctedContent: String
)
