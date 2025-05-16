package com.example.currencyconverter.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object ExchangeRateApi {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6"
    private const val API_KEY = "YOUR-API-KEY"

    suspend fun convertCurrency(amount: String, fromCurrency: String, toCurrency: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL/$API_KEY/pair/$fromCurrency/$toCurrency/$amount"
                val request = Request.Builder().url(url).build()
                val response = OkHttpClient().newCall(request).execute()
                val body = response.body?.string() ?: return@withContext "Eroare răspuns API"

                val json = JSONObject(body)
                if (json.getString("result") != "success") {
                    return@withContext "Eroare conversie"
                }

                val converted = json.getDouble("conversion_result")
                return@withContext "%.2f".format(converted)
            } catch (e: Exception) {
                return@withContext "Eroare: ${e.message}"
            }
        }
    }
}
