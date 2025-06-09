// 일기 수정 api 테스트용 repository

package com.example.team.repository

import com.example.team.model.DiaryApiResponse
import com.example.team.model.Meta
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
    
    private val apiUrl = "https://stable-diffusion.mozilla-4-0-linux.workers.dev/api/aiku/diary-corrector"
    
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
                    correctedTexts.add(dataArray.getString(i))
                }
                
                val apiResponse = DiaryApiResponse(
                    meta = Meta(
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
    
    suspend fun saveDiary(title: String, content: String, correctedContent: String = ""): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val diaryEntity = DiaryEntity(
                    createdDate = currentDate,
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
    
    suspend fun correctAndSaveDiary(title: String, content: String): Result<String> {
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
                val saveResult = saveDiary(title, content, correctedContent)
                
                if (saveResult.isFailure) {
                    return@withContext Result.failure(saveResult.exceptionOrNull() ?: Exception("저장 실패"))
                }
                
                Result.success(correctedContent)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 