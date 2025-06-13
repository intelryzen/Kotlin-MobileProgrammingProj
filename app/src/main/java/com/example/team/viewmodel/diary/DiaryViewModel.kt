package com.example.team.viewmodel.diary

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.team.model.VocabularyItem
import com.example.team.repository.ChatRepository
import com.example.team.repository.DiaryRepository
import com.example.team.repository.VocabularyRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryViewModel(
    private val repository: DiaryRepository,
    private val vocabularyRepository: VocabularyRepository? = null,
    private val chatRepository: ChatRepository? = null
) : ViewModel() {
    var diaryList = mutableStateListOf<DiaryViewState>()
        private set
    var currentDiaryIndex by mutableStateOf(-1)

    val currentDiary: DiaryViewState?
        get() = diaryList.getOrNull(currentDiaryIndex)

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadDiariesFromDB()
    }

    private fun loadDiariesFromDB() {
        viewModelScope.launch {
            try {
                val result = repository.getAllDiaries()
                if (result.isSuccess) {
                    val diaryEntities = result.getOrNull() ?: emptyList()
                    val dateTimeFormat =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val diaryStates = diaryEntities.map { entity ->
                        // DiaryEntity의 createdDate를 Date 객체로 변환
                        val createdDate = dateTimeFormat.parse(entity.createdDate)

                        DiaryViewState(
                            id = entity.id,
                            title = entity.title,
                            content = entity.content,
                            correctedContent = entity.correctedContent,
                            isOriginal = true,
                            wordCollect = false,
                            createdAt = createdDate!!
                        )
                    }
                    diaryList.clear()
                    diaryList.addAll(diaryStates)
                }
            } catch (e: Exception) {
                errorMessage = "일기 목록을 불러오는 중 오류가 발생했습니다: ${e.message}"
            }
        }
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
                    onSuccess("일기를 삭제했습니다.")
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

    // 새 일기 저장
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
                            val dateTimeFormat =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val diaryEntries = diaryEntities.map { entity ->
                                val createdDate = dateTimeFormat.parse(entity.createdDate)
                                DiaryViewState(
                                    id = entity.id,
                                    title = entity.title,
                                    content = entity.content,
                                    correctedContent = entity.correctedContent,
                                    isOriginal = true,
                                    wordCollect = false,
                                    createdAt = createdDate!!
                                )
                            }
                            diaryList.clear()
                            diaryList.addAll(diaryEntries)

                            // 가장 최근에 저장된 일기(첫 번째)를 현재 일기로 설정
                            currentDiaryIndex = if (diaryList.isNotEmpty()) 0 else -1
                        }
                    } catch (e: Exception) {
                        // 로드 실패 시 기존 방식으로 처리해야
                        val newDiary = DiaryViewState(
                            id = getNextId(),
                            title = title,
                            content = content,
                            correctedContent = correctedContent,
                            createdAt = Date()
                        )
                        diaryList.add(0, newDiary)
                        currentDiaryIndex = 0
                    }

                    onSuccess("일기를 교정하고 저장하였습니다.")
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
                )

                if (result.isSuccess) {
                    val updatedCorrectedContent = result.getOrNull() ?: diary.correctedContent

                    // 원본 일기는 알아서 수정됨.
                    diary.correctedContent = updatedCorrectedContent

                    onSuccess("일기를 성공적으로 수정하였습니다.")
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

    // 단어 수집만 하고 선택 팝엎만 표시됨
    fun collectVocabularies(onSuccess: (List<VocabularyItem>) -> Unit, onError: (String) -> Unit) {
        val diary = currentDiary
        if (diary == null) {
            onError("일기가 선택되지 않았습니다.")
            return
        }

        val contentToCorrect = if (!diary.isOriginal && diary.correctedContent.isNotEmpty()) {
            diary.correctedContent
        } else {
            diary.content
        }

        if (contentToCorrect.isBlank()) {
            onError("교정할 일기 내용이 없습니다.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = vocabularyRepository!!.collectVocabulary(contentToCorrect)

                if (result.isSuccess) {
                    val apiResponse = result.getOrNull()
                    val vocabularyItems = apiResponse?.data ?: emptyList()
                    onSuccess(vocabularyItems)
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

    // 선택된 단어들만 저장
    fun saveSelectedVocabulary(
        selectedItems: List<VocabularyItem>,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit,
        onVocabularyUpdated: (() -> Unit)? = null
    ) {
        if (selectedItems.isEmpty()) {
            onError("선택한 단어가 없습니다.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = vocabularyRepository!!.saveSelectedVocabularyWords(selectedItems)

                if (result.isSuccess) {
                    val message = result.getOrNull() ?: "선택한 단어들이 저장되었습니다."
                    // 반드시 단어 뷰모델 갱신해줘야 함.
                    onVocabularyUpdated?.invoke()
                    onSuccess(message)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "단어 저장 중 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "단어 저장 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }

    // Q&A
    fun askQuestion(
        question: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val diary = currentDiary
        if (diary == null) {
            onError("일기가 선택되지 않았습니다.")
            return
        }

        if (question.isBlank()) {
            onError("질문을 입력해주세요.")
            return
        }

        val userDiary = diary.content
        val gptDiary = diary.correctedContent

        if (userDiary.isBlank()) {
            onError("원본 일기 내용이 없습니다.")
            return
        }

        if (gptDiary.isBlank()) {
            onError("교정된 일기 내용이 없습니다.")
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val result = chatRepository!!.askQuestion(userDiary, gptDiary, question)

                if (result.isSuccess) {
                    val apiResponse = result.getOrNull()
                    val answer = apiResponse?.data?.firstOrNull() ?: "답변이 없습니다."
                    onSuccess(answer)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "답변 중 오류가 발생했습니다."
                    errorMessage = error
                    onError(error)
                }
            } catch (e: Exception) {
                val error = "답변 중 오류가 발생했습니다: ${e.message}"
                errorMessage = error
                onError(error)
            } finally {
                isLoading = false
            }
        }
    }
}