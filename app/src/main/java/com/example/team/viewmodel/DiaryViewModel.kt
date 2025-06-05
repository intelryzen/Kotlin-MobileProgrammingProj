package com.example.team.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.repository.DiaryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DiaryUiState(
    val originalText: String = "",
    val correctedText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class DiaryViewModel : ViewModel() {
    
    private val repository = DiaryRepository()
    
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()
    
    fun updateOriginalText(text: String) {
        _uiState.value = _uiState.value.copy(originalText = text)
    }
    
    fun correctDiary() {
        val currentText = _uiState.value.originalText
        if (currentText.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "일기 내용을 입력해주세요.")
            return
        }
        
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isSuccess = false
        )
        
        viewModelScope.launch {
            repository.correctDiary(currentText)
                .onSuccess { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        correctedText = response.data.firstOrNull() ?: "",
                        isSuccess = true,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "오류가 발생했습니다: ${error.message}",
                        isSuccess = false
                    )
                }
        }
    }
    
    fun reset() {
        _uiState.value = DiaryUiState()
    }
} 