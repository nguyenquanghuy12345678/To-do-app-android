package com.example.to_do_app.data.database

import com.example.to_do_app.data.model.UnsplashResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object UnsplashApiClient {
    private const val API_KEY = "7xS3ot7pcKjak9M6qoqFQCW9rQnhSERkj2GBMr5ZeA4" // Thay bằng API Key của bạn
    private const val BASE_URL = "https://api.unsplash.com"
    private val client = OkHttpClient()
    private val gson = Gson()

    suspend fun getPhotoUrl(query: String): String? = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/search/photos?query=${query}&per_page=1&client_id=$API_KEY"
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                val unsplashResponse = gson.fromJson(json, UnsplashResponse::class.java)
                unsplashResponse.results.firstOrNull()?.urls?.regular
            } else {
                null
            }
        } catch (e: IOException) {
            null
        }
    }
}