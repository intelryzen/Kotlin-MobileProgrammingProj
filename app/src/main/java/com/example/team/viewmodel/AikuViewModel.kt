package com.example.team.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.team.AikuDatabase
import com.example.team.roomDB.ChatEntity
import com.example.team.roomDB.DiaryEntity
import com.example.team.roomDB.VocabEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AikuViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AikuDatabase.getDatabase(application)

    val diaries: LiveData<List<DiaryEntity>> = db.diaryDao().getAll()
    val vocabs: LiveData<List<VocabEntity>> = db.vocabDao().getAllSortedByDate()
    val chats: LiveData<List<ChatEntity>> = db.chatDao().getAll()

    fun insertDiary(title: String, content: String, corrected: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        viewModelScope.launch(Dispatchers.IO) {
            db.diaryDao().insert(
                DiaryEntity(
                    title = title,
                    content = content,
                    correctedContent = corrected,
                    createdDate = date
                )
            )
        }
    }

    fun updateDiary(diary: DiaryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.diaryDao().update(diary)
        }
    }

    fun deleteDiary(diary: DiaryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.diaryDao().delete(diary)
        }
    }

    fun insertVocab(word: String, part: String, example: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        viewModelScope.launch(Dispatchers.IO) {
            db.vocabDao().insert(
                VocabEntity(
                    word = word,
                    partOfSpeech = part,
                    example = example,
                    createdDate = date
                )
            )
        }
    }

    fun updateVocab(vocab: VocabEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.vocabDao().update(vocab)
        }
    }

    fun deleteVocab(vocab: VocabEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.vocabDao().delete(vocab)
        }
    }

    fun insertChat(diaryId: Int?, isQuestion: Boolean, content: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().insert(
                ChatEntity(
                    diaryId = diaryId,
                    isQuestion = isQuestion,
                    content = content,
                    createdDate = date
                )
            )
        }
    }

    fun updateChat(chat: ChatEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().update(chat)
        }
    }

    fun deleteChat(chat: ChatEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            db.chatDao().delete(chat)
        }
    }
}
