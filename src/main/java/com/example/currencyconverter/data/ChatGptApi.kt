package com.example.currencyconverter.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject

object ChatGptApi {
    private const val apiKey = "YOUR-API-KEY"
    private val client = OkHttpClient()

    suspend fun extractNumberOnly(text: String, language: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = """
                    From the following text: "$text", extract only the number as digits, strictly with no extra characters, allow "." and "," as zecimal separator, if the separator is a "," make it as a ".". If there is no number, return "" (empty string). Answer only with the number, nothing else.
                """.trimIndent()

                val json = JSONObject().apply {
                    put("model", "gpt-3.5-turbo")
                    put("messages", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("content", prompt)
                        })
                    })
                    put("max_tokens", 12)
                }

                val body = RequestBody.create(
                    "application/json".toMediaTypeOrNull(),
                    json.toString()
                )
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) return@withContext ""
                val content = JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim()

                return@withContext content.replace("[^0-9.]".toRegex(), "")
            } catch (e: Exception) {
                return@withContext ""
            }
        }
    }
}
