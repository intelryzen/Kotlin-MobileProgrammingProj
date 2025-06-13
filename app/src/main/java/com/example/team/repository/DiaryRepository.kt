// 일기 수정 api 테스트용 repository

package com.example.team.repository

import com.example.team.model.DiaryApiResponse
import com.example.team.model.DiaryMeta
import com.example.team.roomDB.DiaryDao
import com.example.team.roomDB.DiaryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DiaryRepository(private val diaryDao: DiaryDao) {
    
    private val apiUrl = "https://diary-corrector-473344676717.asia-northeast1.run.app/"
    
    suspend fun correctDiary(diaryText: String): Result<DiaryApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("diary", diaryText)
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
                
                val correctedTexts = mutableListOf<String>()
                for (i in 0 until dataArray.length()) {
                    val text = dataArray.getString(i)
                    // 양끝의 큰따옴표 제거
                    val cleanedText = text.removeSurrounding("\"")
                    correctedTexts.add(cleanedText)
                }
                
                val apiResponse = DiaryApiResponse(
                    meta = DiaryMeta(
                        status = meta.getInt("status"),
                        message = meta.getString("message")
                    ),
                    data = correctedTexts
                )
                
                Result.success(apiResponse)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun saveDiary(title: String, content: String, correctedContent: String = "", createdAt: Date = Date()): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val currentDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(createdAt)
                println("저장할 날짜 문자열: $currentDateTime")
                val diaryEntity = DiaryEntity(
                    createdDate = currentDateTime,
                    title = title,
                    content = content,
                    correctedContent = correctedContent
                )
                diaryDao.insert(diaryEntity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getAllDiaries(): Result<List<DiaryEntity>> {
        return withContext(Dispatchers.IO) {
            try {
                // LiveData를 사용하지 않고 직접 쿼리하는 메서드가 필요함
                // 일단 기존 메서드를 사용하되, 동기적으로 데이터를 가져오도록 수정 필요
                val diaries = diaryDao.getAllSync()
                Result.success(diaries)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun correctAndSaveDiary(title: String, content: String, createdAt: Date = Date()): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. API 호출하여 일기 수정
                val correctionResult = correctDiary(content)
                
                if (correctionResult.isFailure) {
                    return@withContext Result.failure(correctionResult.exceptionOrNull() ?: Exception("API 호출 실패"))
                }
                
                val apiResponse = correctionResult.getOrNull()
                val correctedContent = apiResponse?.data?.firstOrNull() ?: content
                
                // 2. 데이터베이스에 저장
                val saveResult = saveDiary(title, content, correctedContent, createdAt)
                
                if (saveResult.isFailure) {
                    return@withContext Result.failure(saveResult.exceptionOrNull() ?: Exception("저장 실패"))
                }
                
                Result.success(correctedContent)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun deleteDiary(diaryId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // ID로 일기를 찾아서 삭제
                val diaries = diaryDao.getAllSync()
                val diaryToDelete = diaries.find { it.id == diaryId }
                
                if (diaryToDelete != null) {
                    diaryDao.delete(diaryToDelete)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("삭제할 일기를 찾을 수 없습니다."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateDiary(diaryId: Int, title: String, content: String, correctedContent: String? = null): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                // ID로 일기를 찾기
                val diaries = diaryDao.getAllSync()
                val diaryToUpdate = diaries.find { it.id == diaryId }
                
                if (diaryToUpdate == null) {
                    return@withContext Result.failure(Exception("수정할 일기를 찾을 수 없습니다."))
                }

                // 수정된 내용이 제공되지 않았다면 API 호출하여 교정
                val finalCorrectedContent = if (correctedContent != null) {
                    correctedContent
                } else {
                    val correctionResult = correctDiary(content)
                    if (correctionResult.isSuccess) {
                        val apiResponse = correctionResult.getOrNull()
                        apiResponse?.data?.firstOrNull() ?: content
                    } else {
                        content // API 호출 실패 시 원본 내용 사용
                    }
                }

                // 기존 일기 엔티티를 복사하여 업데이트
                val updatedEntity = diaryToUpdate.copy(
                    title = title,
                    content = content,
                    correctedContent = finalCorrectedContent
                )

                diaryDao.update(updatedEntity)
                Result.success(finalCorrectedContent)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 