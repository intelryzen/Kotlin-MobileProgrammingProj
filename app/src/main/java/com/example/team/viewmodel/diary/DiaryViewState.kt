package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Date

class DiaryViewState(
    val id: Int,
    title: String = "",
    content: String = "",
    correctedContent: String = "",
    isOriginal: Boolean = true,
    wordCollect: Boolean = false,
    val createdAt: Date = Date()
) {
    var title by mutableStateOf(title)
    var content by mutableStateOf(content)
    var correctedContent by mutableStateOf(correctedContent)
    var isOriginal by mutableStateOf(isOriginal)
    var wordCollect by mutableStateOf(wordCollect)
}
