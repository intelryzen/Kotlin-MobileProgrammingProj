package com.example.team.viewmodel.diary

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.team.AikuDatabase
import com.example.team.model.DiaryApiResponse
import com.example.team.model.Meta
import com.example.team.repository.DiaryRepository
import com.example.team.roomDB.ChatEntity
import com.example.team.roomDB.DiaryEntity
import com.example.team.roomDB.VocabEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// UI 상태를 나타내는 데이터 클래스
data class DiaryUiState(
    val originalText: String = "",
    val correctedText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AikuDatabase.getDatabase(application)
    private val diaryDao = db.diaryDao()
    private val vocabDao = db.vocabDao()
    private val chatDao = db.chatDao()
    private val repository = DiaryRepository()

    // LiveData로 전체 데이터 관찰
    val diaries: LiveData<List<DiaryEntity>> = diaryDao.getAll()
    val vocabs: LiveData<List<VocabEntity>> = vocabDao.getAllSortedByDate()
    val chats: LiveData<List<ChatEntity>> = chatDao.getAll()

    // 현재 일기 선택 상태
    var currentDiaryIndex = mutableStateOf(-1)
    val currentDiary: DiaryEntity?
        get() = diaries.value?.getOrNull(currentDiaryIndex.value)

    // 교정 UI 상태
    var uiState = mutableStateOf(DiaryUiState())

    // 일기 추가
    fun insertDiary(diary: DiaryEntity) = viewModelScope.launch {
        diaryDao.insert(diary)
    }

    // 단어 추가
    fun insertVocab(vocab: VocabEntity) = viewModelScope.launch {
        vocabDao.insert(vocab)
    }

    // 채팅 추가
    fun insertChat(chat: ChatEntity) = viewModelScope.launch {
        chatDao.insert(chat)
    }

    // 일기 삭제
    fun deleteDiary(diary: DiaryEntity) = viewModelScope.launch {
        diaryDao.delete(diary)
    }

    // 일기 교정 요청
    fun correctDiary(diaryText: String) {
        uiState.value = uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            isSuccess = false
        )

        viewModelScope.launch {
            repository.correctDiary(diaryText)
                .onSuccess { response ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        correctedText = response.data.firstOrNull() ?: "",
                        isSuccess = true
                    )
                }
                .onFailure { e ->
                    uiState.value = uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "알 수 없는 오류",
                        isSuccess = false
                    )
                }
        }
    }

    fun updateOriginalText(text: String) {
        uiState.value = uiState.value.copy(originalText = text)
    }

    fun resetCorrection() {
        uiState.value = DiaryUiState()
    }
}
