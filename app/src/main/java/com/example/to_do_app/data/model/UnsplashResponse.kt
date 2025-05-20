package com.example.to_do_app.data.model

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)