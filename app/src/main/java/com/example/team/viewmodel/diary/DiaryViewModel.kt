package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.repository.DiaryRepository
import kotlinx.coroutines.launch

class DiaryViewModel(private val repository: DiaryRepository) : ViewModel() {
    var diaryList = mutableStateListOf<DiaryEntry>()
        private set
    var currentDiaryIndex by mutableStateOf(-1)
    val currentDiary : DiaryEntry?
        get() = diaryList.getOrNull(currentDiaryIndex)
    
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

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
    
    fun saveDiary(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val diary = currentDiary ?: return
        
        if (diary.title.isBlank() || diary.content.isBlank()) {
            onError("제목과 내용을 모두 입력해주세요.")
            return
        }
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val result = repository.correctAndSaveDiary(diary.title, diary.content)
                
                if (result.isSuccess) {
                    val correctedContent = result.getOrNull() ?: diary.content
                    diary.editedContent = correctedContent
                    onSuccess("일기가 성공적으로 저장되었습니다!")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "알 수 없는 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "저장 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }
}