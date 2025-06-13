package com.example.team.repository

import com.example.team.model.ChatApiResponse
import com.example.team.model.ChatMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.jsoup.Connection
import org.jsoup.Jsoup

// chat api 레포
// 로컬DB에 따로 저장하지 않으므로 dao 주입 필요없음
class ChatRepository {
    
    private val apiUrl = "https://chat-473344676717.asia-northeast1.run.app"
    
    suspend fun askQuestion(userDiary: String, gptDiary: String, question: String): Result<ChatApiResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val requestBody = JSONObject().apply {
                    put("userDiary", userDiary)
                    put("gptDiary", gptDiary)
                    put("question", question)
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
                
                val answers = mutableListOf<String>()
                for (i in 0 until dataArray.length()) {
                    answers.add(dataArray.getString(i))
                }
                
                val apiResponse = ChatApiResponse(
                    meta = ChatMeta(
                        status = meta.getInt("status"),
                        message = meta.getString("message")
                    ),
                    data = answers
                )
                
                Result.success(apiResponse)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
} 