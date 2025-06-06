package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class DiaryEntry(
    val id: Int,
    title: String = "",
    content: String = "",
    editedContent: String = "",
    isOriginal: Boolean = true,
    wordCollect: Boolean = false
) {
    var title by mutableStateOf(title)
    var content by mutableStateOf(content)
    var editedContent by mutableStateOf(editedContent)
    var isOriginal by mutableStateOf(isOriginal)
    var wordCollect by mutableStateOf(wordCollect)
}
