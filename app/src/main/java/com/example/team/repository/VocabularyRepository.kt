package com.example.team.repository

import com.example.team.model.VocabularyApiResponse
import com.example.team.model.VocabularyMeta
import com.example.team.model.VocabularyItem
import com.example.team.roomDB.VocabDao
import com.example.team.roomDB.VocabEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 단어장 레포
class VocabularyRepository(private val vocabDao: VocabDao) {
    
    private val apiUrl = "https://vocabulary-473344676717.asia-northeast1.run.app"
    
    suspend fun collectVocabulary(diaryContent: String): Result<VocabularyApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("diary", diaryContent)
                }
                
                val response = Jsoup.connect(apiUrl)
                    .method(Connection.Method.POST)
                    .header("Content-Type", "application/json")
                    .requestBody(requestBody.toString())
                    .ignoreContentType(true)
                    .execute()
                
                val responseBody = response.body()
                val jsonResponse = JSONObject(responseBody)
                
                val meta = jsonResponse.getJSONObject("meta")
                val dataArray = jsonResponse.getJSONArray("data")
                
                val vocabularyItems = mutableListOf<VocabularyItem>()
                for (i in 0 until dataArray.length()) {
                    val item = dataArray.getJSONObject(i)
                    vocabularyItems.add(
                        VocabularyItem(
                            word = item.getString("word"),
                            partOfSpeech = item.getString("partOfSpeech"),
                            meaning = item.getString("meaning"),
                            example = item.getString("exampleSentence")
                        )
                    )
                }
                
                val apiResponse = VocabularyApiResponse(
                    meta = VocabularyMeta(
                        status = meta.getInt("status"),
                        message = meta.getString("message")
                    ),
                    data = vocabularyItems
                )
                
                Result.success(apiResponse)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun saveVocabularyWords(vocabularyItems: List<VocabularyItem>): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val newWords = mutableListOf<VocabEntity>()
                
                // 중복 단어 체크하고 새로운 단어만 추가
                for (item in vocabularyItems) {
                    val exists = vocabDao.isWordExists(item.word)
                    if (!exists) {
                        newWords.add(
                            VocabEntity(
                                word = item.word,
                                partOfSpeech = item.partOfSpeech,
                                meaning = item.meaning,
                                example = item.example,
                                createdDate = currentDateTime
                            )
                        )
                    }
                }
                
                if (newWords.isNotEmpty()) {
                    vocabDao.insertAll(newWords)
                }
                
                Result.success(newWords.size)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun saveSelectedVocabularyWords(selectedItems: List<VocabularyItem>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val saveResult = saveVocabularyWords(selectedItems)
                
                if (saveResult.isFailure) {
                    return@withContext Result.failure(saveResult.exceptionOrNull() ?: Exception("단어 저장 실패"))
                }
                
                val newWordsCount = saveResult.getOrNull() ?: 0
                val totalWordsCount = selectedItems.size
                
                val message = if (newWordsCount == 0) {
                    "선택한 모든 단어가 이미 단어장에 존재합니다."
                } else if (newWordsCount == totalWordsCount) {
                    "${newWordsCount}개의 새로운 단어를 단어장에 추가했습니다."
                } else {
                    "선택한 ${totalWordsCount}개 단어 중 ${newWordsCount}개의 새로운 단어를 단어장에 추가했습니다."
                }
                
                Result.success(message)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getAllVocabulary(): Result<List<VocabEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                val vocabularies = vocabDao.getAll()
                Result.success(vocabularies)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteVocabularies(words: List<String>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val vocabs = vocabDao.getAll().filter { it.word in words }
                if (vocabs.isNotEmpty()) {
                    vocabs.forEach { vocab ->
                        vocabDao.delete(vocab)
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("에러1"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 