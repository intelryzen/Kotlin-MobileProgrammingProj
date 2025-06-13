package com.example.team.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.model.VocabularyItem
import com.example.team.repository.VocabularyRepository
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {
    
    var vocabularyList = mutableStateListOf<VocabularyItem>()
        private set
    
    var isLoading by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    init {
        loadVocabularyFromDatabase()
    }
    
    private fun loadVocabularyFromDatabase() {
        viewModelScope.launch {
            try {
                val result = repository.getAllVocabulary()
                if (result.isSuccess) {
                    val vocabEntities = result.getOrNull() ?: emptyList()
                    val vocabularies = vocabEntities.map { entity ->
                        VocabularyItem(
                            word = entity.word,
                            partOfSpeech = entity.partOfSpeech,
                            meaning = entity.meaning,
                            example = entity.example
                        )
                    }
                    vocabularyList.clear()
                    vocabularyList.addAll(vocabularies)
                }
            } catch (e: Exception) {
                errorMessage = "단어장을 불러오는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    fun collectVocabularyFromDiary(diaryContent: String, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        if (diaryContent.isBlank()) {
            onError("일기 내용이 비어있습니다.")
            return
        }
        
        isLoading = true
        errorMessage = null
        
        viewModelScope.launch {
            try {
                val result = repository.collectAndSaveVocabulary(diaryContent)
                
                if (result.isSuccess) {
                    val message = result.getOrNull() ?: "단어 수집이 완료되었습니다."
                    
                    // 데이터베이스에서 다시 로드하여 UI 업데이트
                    loadVocabularyFromDatabase()
                    
                    onSuccess(message)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "단어 수집 중 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "단어 수집 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }
    
    fun refreshVocabulary() {
        loadVocabularyFromDatabase()
    }
} 