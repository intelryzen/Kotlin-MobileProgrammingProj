package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.repository.DiaryRepository
import com.example.team.roomDB.DiaryEntity
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
    
    init {
        loadDiariesFromDatabase()
    }
    
    private fun loadDiariesFromDatabase() {
        viewModelScope.launch {
            try {
                val result = repository.getAllDiaries()
                if (result.isSuccess) {
                    val diaryEntities = result.getOrNull() ?: emptyList()
                    val diaryEntries = diaryEntities.map { entity ->
                        DiaryEntry(
                            id = entity.id,
                            title = entity.title,
                            content = entity.content,
                            editedContent = entity.correctedContent,
                            isOriginal = true,
                            wordCollect = false
                        )
                    }
                    diaryList.clear()
                    diaryList.addAll(diaryEntries)
                }
            } catch (e: Exception) {
                errorMessage = "일기 목록을 불러오는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }

    fun createNewDiary(){
        val newDiary = DiaryEntry(id = getNextId())
        diaryList.add(0, newDiary) // 맨 앞에 추가 (최신순)
        currentDiaryIndex = 0
    }
    
    private fun getNextId(): Int {
        return if (diaryList.isEmpty()) 0 else diaryList.maxOf { it.id } + 1
    }

    fun deleteCurrentDiary(){
        if(currentDiaryIndex in diaryList.indices){
            diaryList.removeAt(currentDiaryIndex)
            currentDiaryIndex = if (diaryList.isNotEmpty()) {
                if (currentDiaryIndex >= diaryList.size) diaryList.size - 1 else currentDiaryIndex
            } else -1
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
                    // 저장 후 데이터베이스에서 다시 로드하여 동기화
                    loadDiariesFromDatabase()
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