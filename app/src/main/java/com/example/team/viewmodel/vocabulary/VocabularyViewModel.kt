package com.example.team.viewmodel.vocabulary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.repository.VocabularyRepository
import com.example.team.model.VocabularyItem
import kotlinx.coroutines.launch

class VocabularyViewModel(private val repository: VocabularyRepository) : ViewModel() {
    
    var vocabularyList = mutableStateListOf<VocabularyItem>()
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    init {
        loadVocabularyFromDB()
    }
    
    private fun loadVocabularyFromDB() {
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

    fun refreshVocabulary() {
        loadVocabularyFromDB()
    }
} 