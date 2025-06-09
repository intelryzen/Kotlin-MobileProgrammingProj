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
import kotlinx.coroutines.launch

// Compose UI를 위한 상태 보관용 Entry 클래스
class DiaryEntry(
    val id: Int,
    title: String = "",
    content: String = "",
    editedContent: String = "",
    isOriginal: Boolean = true,
    wordCollect: Boolean = false
) {
    var title = mutableStateOf(title)
    var content = mutableStateOf(content)
    var editedContent = mutableStateOf(editedContent)
    var isOriginal = mutableStateOf(isOriginal)
    var wordCollect = mutableStateOf(wordCollect)

    fun toEntity(): DiaryEntity = DiaryEntity(
        id = id,
        title = title.value,
        content = content.value,
        correctedContent = editedContent.value,
        createdDate = "2025-06-09"
    )

    companion object {
        fun fromEntity(entity: DiaryEntity): DiaryEntry = DiaryEntry(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            editedContent = entity.correctedContent
        )
    }
}

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

    // Room 기반 데이터
    val diaries: LiveData<List<DiaryEntity>> = diaryDao.getAll()
    val vocabs: LiveData<List<VocabEntity>> = vocabDao.getAllSortedByDate()
    val chats: LiveData<List<ChatEntity>> = chatDao.getAll()

    // Compose용 상태
    var diaryUiEntry = mutableStateOf<DiaryEntry?>(null)
    var currentDiaryIndex = mutableStateOf(-1)

    fun loadDiaryAsEntry(index: Int) {
        val entity = diaries.value?.getOrNull(index) ?: return
        currentDiaryIndex.value = index
        diaryUiEntry.value = DiaryEntry.fromEntity(entity)
    }

    fun saveCurrentDiary() {
        diaryUiEntry.value?.let {
            insertDiary(it.toEntity())
        }
    }

    fun insertDiary(diary: DiaryEntity) = viewModelScope.launch {
        diaryDao.insert(diary)
    }

    fun insertVocab(vocab: VocabEntity) = viewModelScope.launch {
        vocabDao.insert(vocab)
    }

    fun insertChat(chat: ChatEntity) = viewModelScope.launch {
        chatDao.insert(chat)
    }

    fun deleteDiary(diary: DiaryEntity) = viewModelScope.launch {
        diaryDao.delete(diary)
    }

    var uiState = mutableStateOf(DiaryUiState())

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