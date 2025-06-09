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
                            exampleSentence = item.getString("exampleSentence")
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
                                example = item.exampleSentence,
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
    
    suspend fun collectAndSaveVocabulary(diaryContent: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. API 호출하여 단어 수집
                val collectionResult = collectVocabulary(diaryContent)
                
                if (collectionResult.isFailure) {
                    return@withContext Result.failure(collectionResult.exceptionOrNull() ?: Exception("단어 수집 API 호출 실패"))
                }
                
                val apiResponse = collectionResult.getOrNull()
                val vocabularyItems = apiResponse?.data ?: emptyList()
                
                // 2. 데이터베이스에 저장
                val saveResult = saveVocabularyWords(vocabularyItems)
                
                if (saveResult.isFailure) {
                    return@withContext Result.failure(saveResult.exceptionOrNull() ?: Exception("단어 저장 실패"))
                }
                
                val newWordsCount = saveResult.getOrNull() ?: 0
                val totalWordsCount = vocabularyItems.size
                
                val message = if (newWordsCount == 0) {
                    "모든 단어가 이미 존재합니다."
                } else {
                    "총 ${totalWordsCount}개 단어 중 ${newWordsCount}개의 새로운 단어를 추가했습니다."
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
                val vocabularies = vocabDao.getAllSync()
                Result.success(vocabularies)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 