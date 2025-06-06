package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DiaryViewModel : ViewModel() {
    var diaryList = mutableStateListOf<DiaryEntry>()
        private set
    var currentDiaryIndex by mutableStateOf(-1)
    val currentDiary : DiaryEntry?
        get() = diaryList.getOrNull(currentDiaryIndex)

    fun createNewDiary(){
        val newDiary = DiaryEntry(id = diaryList.size)
        diaryList.add(newDiary)
        currentDiaryIndex = diaryList.lastIndex
    }

    fun deleteCurrentDiary(){
        if(currentDiaryIndex in diaryList.indices){
            diaryList.removeAt(currentDiaryIndex)
            currentDiaryIndex = if (diaryList.isNotEmpty()) 0 else -1
        }
    }
}