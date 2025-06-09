package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.repository.DiaryRepository
import com.example.team.repository.VocabularyRepository
import com.example.team.roomDB.DiaryEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(
    private val repository: DiaryRepository,
    private val vocabularyRepository: VocabularyRepository? = null
) : ViewModel() {
    var diaryList = mutableStateListOf<DiaryEntry>()
        private set
    var currentDiaryIndex by mutableStateOf(-1)

    val currentDiary: DiaryEntry?
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
                    val dateTimeFormat =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val diaryEntries = diaryEntities.map { entity ->
                        // DiaryEntity의 createdDate를 Date 객체로 변환
                        val createdDate = dateTimeFormat.parse(entity.createdDate)

                        DiaryEntry(
                            id = entity.id,
                            title = entity.title,
                            content = entity.content,
                            editedContent = entity.correctedContent,
                            isOriginal = true,
                            wordCollect = false,
                            createdAt = createdDate
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

    fun createNewDiary() {
        val newDiary = DiaryEntry(
            id = getNextId(),
            createdAt = Date() // 새 일기는 현재 시간으로 설정
        )
        diaryList.add(0, newDiary) // 맨 앞에 추가 (최신순)
        currentDiaryIndex = 0
    }

    private fun getNextId(): Int {
        return if (diaryList.isEmpty()) 0 else diaryList.maxOf { it.id } + 1
    }

    fun deleteCurrentDiary(onSuccess: (String) -> Unit = {}, onError: (String) -> Unit = {}) {
        val diary = currentDiary
        if (diary == null) {
            onError("삭제할 일기가 없습니다.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = repository.deleteDiary(diary.id)
                
                if (result.isSuccess) {
                    // 메모리에서도 제거
                    if (currentDiaryIndex in diaryList.indices) {
                        diaryList.removeAt(currentDiaryIndex)
                        currentDiaryIndex = if (diaryList.isNotEmpty()) {
                            if (currentDiaryIndex >= diaryList.size) diaryList.size - 1 else currentDiaryIndex
                        } else -1
                    }
                    onSuccess("일기가 성공적으로 삭제되었습니다.")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "삭제 중 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "삭제 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }

    // 새 일기 저장 (작성 화면에서 사용)
    fun saveNewDiary(
        title: String,
        content: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        if (title.isBlank() || content.isBlank()) {
            onError("제목과 내용을 모두 입력해주세요.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = repository.correctAndSaveDiary(title, content)

                if (result.isSuccess) {
                    val correctedContent = result.getOrNull() ?: content
                    
                    // 데이터베이스에서 최신 데이터를 다시 로드하여 동기화
                    try {
                        val diariesResult = repository.getAllDiaries()
                        if (diariesResult.isSuccess) {
                            val diaryEntities = diariesResult.getOrNull() ?: emptyList()
                            val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val diaryEntries = diaryEntities.map { entity ->
                                val createdDate = dateTimeFormat.parse(entity.createdDate)
                                DiaryEntry(
                                    id = entity.id,
                                    title = entity.title,
                                    content = entity.content,
                                    editedContent = entity.correctedContent,
                                    isOriginal = true,
                                    wordCollect = false,
                                    createdAt = createdDate
                                )
                            }
                            diaryList.clear()
                            diaryList.addAll(diaryEntries)
                            
                            // 가장 최근에 저장된 일기 (첫 번째)를 현재 일기로 설정
                            currentDiaryIndex = if (diaryList.isNotEmpty()) 0 else -1
                        }
                    } catch (e: Exception) {
                        // 로드 실패 시 기존 방식으로 처리
                        val newDiary = DiaryEntry(
                            id = getNextId(),
                            title = title,
                            content = content,
                            editedContent = correctedContent,
                            createdAt = Date()
                        )
                        diaryList.add(0, newDiary)
                        currentDiaryIndex = 0
                    }

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

    // 기존 일기 저장 (수정 시 사용)
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

    // 기존 일기 수정
    fun updateCurrentDiary(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val diary = currentDiary
        if (diary == null) {
            onError("수정할 일기가 없습니다.")
            return
        }

        if (diary.title.isBlank() || diary.content.isBlank()) {
            onError("제목과 내용을 모두 입력해주세요.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = repository.updateDiary(
                    diaryId = diary.id,
                    title = diary.title,
                    content = diary.content,
                    correctedContent = diary.editedContent.takeIf { it.isNotEmpty() }
                )

                if (result.isSuccess) {
                    val updatedCorrectedContent = result.getOrNull() ?: diary.editedContent
                    // 메모리의 일기도 업데이트
                    diary.editedContent = updatedCorrectedContent
                    
                    onSuccess("일기가 성공적으로 수정되었습니다!")
                } else {
                    val error = result.exceptionOrNull()?.message ?: "수정 중 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "수정 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }

    // 단어 수집 기능
    fun collectVocabularyFromCurrentDiary(onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        val diary = currentDiary
        if (diary == null) {
            onError("일기가 선택되지 않았습니다.")
            return
        }

        if (vocabularyRepository == null) {
            onError("단어 수집 기능을 사용할 수 없습니다.")
            return
        }

        val contentToAnalyze = if (!diary.isOriginal && diary.editedContent.isNotEmpty()) {
            diary.editedContent
        } else {
            diary.content
        }

        if (contentToAnalyze.isBlank()) {
            onError("분석할 일기 내용이 없습니다.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = vocabularyRepository.collectAndSaveVocabulary(contentToAnalyze)

                if (result.isSuccess) {
                    val message = result.getOrNull() ?: "단어 수집이 완료되었습니다."
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
}