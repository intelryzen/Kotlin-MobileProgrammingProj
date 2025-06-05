package com.example.team.repository

import com.example.team.model.DiaryApiResponse
import com.example.team.model.Meta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup

class DiaryRepository {
    
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
} 