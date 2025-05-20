package com.example.to_do_app.data.database

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

object WeatherApiClient {
    private const val BASE_URL_WEATHER = "https://api.open-meteo.com/v1/forecast"
    private const val BASE_URL_GEOCODING = "https://nominatim.openstreetmap.org/search"
    private val client = OkHttpClient()

    suspend fun getWeather(city: String, date: String): String? = withContext(Dispatchers.IO) {
        try {
            // Bước 1: Lấy tọa độ từ Nominatim
            val coordinates = getCoordinates(city) ?: run {
                println("Không tìm thấy tọa độ cho địa điểm: $city")
                return@withContext null
            }
            val latitude = coordinates.first
            val longitude = coordinates.second
            println("Tọa độ cho $city: ($latitude, $longitude)")

            // Bước 2: Chuyển đổi date sang định dạng Open-Meteo
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val targetDate = dateFormat.parse(date) ?: run {
                println("Định dạng ngày không hợp lệ: $date")
                return@withContext null
            }
            val targetTimestamp = targetDate.time / 1000

            // Bước 3: Gọi API Open-Meteo để lấy thời tiết
            val weatherUrl = "$BASE_URL_WEATHER?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,weathercode&forecast_days=16&timezone=UTC"
            val weatherRequest = Request.Builder().url(weatherUrl).build()
            val weatherResponse = client.newCall(weatherRequest).execute()

            if (weatherResponse.isSuccessful) {
                val json = weatherResponse.body?.string() ?: return@withContext null
                val jsonObject = JSONObject(json)
                val hourly = jsonObject.getJSONObject("hourly")
                val times = hourly.getJSONArray("time")
                val temperatures = hourly.getJSONArray("temperature_2m")
                val weatherCodes = hourly.getJSONArray("weathercode")

                // Tìm index gần nhất với ngày targetDate
                var closestIndex = -1
                var minDiff = Long.MAX_VALUE
                for (i in 0 until times.length()) {
                    val timeStr = times.getString(i)
                    val time = dateFormat.parse(timeStr.substring(0, 10))?.time?.div(1000) ?: continue
                    val diff = Math.abs(time - targetTimestamp)
                    if (diff < minDiff) {
                        minDiff = diff
                        closestIndex = i
                    }
                }

                if (closestIndex == -1) {
                    println("Không tìm thấy dữ liệu thời tiết cho ngày: $date")
                    return@withContext null
                }

                val temperature = temperatures.getDouble(closestIndex)
                val weatherCode = weatherCodes.getInt(closestIndex)
                val weatherDescription = getWeatherDescription(weatherCode)
                "Nhiệt độ: ${temperature.toInt()}°C, $weatherDescription"
            } else {
                println("Lỗi API Open-Meteo: ${weatherResponse.code} - ${weatherResponse.message}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Lỗi ngoại lệ: ${e.message}")
            null
        }
    }

    // Hàm lấy tọa độ từ Nominatim API
    private suspend fun getCoordinates(city: String): Pair<Double, Double>? = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL_GEOCODING?q=${city}&format=json&limit=1"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "ToDoApp/1.0 (your-email@example.com)") // Nominatim yêu cầu User-Agent
                .build()
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val json = response.body?.string() ?: return@withContext null
                val jsonArray = JSONArray(json)
                if (jsonArray.length() > 0) {
                    val location = jsonArray.getJSONObject(0)
                    val lat = location.getDouble("lat")
                    val lon = location.getDouble("lon")
                    Pair(lat, lon)
                } else {
                    println("Không tìm thấy địa điểm: $city")
                    null
                }
            } else {
                println("Lỗi API Nominatim: ${response.code} - ${response.message}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Lỗi khi gọi Nominatim: ${e.message}")
            null
        }
    }

    // Hàm ánh xạ weathercode sang mô tả (dựa trên tài liệu Open-Meteo)
    private fun getWeatherDescription(code: Int): String {
        return when (code) {
            0 -> "Trời quang"
            1, 2, 3 -> "Nhiều mây"
            45, 48 -> "Sương mù"
            51, 53, 55 -> "Mưa nhỏ"
            61, 63, 65 -> "Mưa vừa"
            80, 81, 82 -> "Mưa rào"
            else -> "Không xác định"
        }
    }
}

//object WeatherApiClient {
//    private const val BASE_URL = "https://api.open-meteo.com/v1/forecast"
//
//    suspend fun getWeather(city: String, date: String): String? = withContext(Dispatchers.IO) {
//        try {
//            // Chuyển đổi tên thành phố thành tọa độ (cần Geocoding đơn giản, tạm thời dùng thủ công)
//            val coordinates = getCoordinates(city) ?: return@withContext null
//            val latitude = coordinates.first
//            val longitude = coordinates.second
//
//            // Chuyển đổi date sang định dạng Open-Meteo
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val targetDate = dateFormat.parse(date)?.time ?: return@withContext null
//            val targetTimestamp = targetDate / 1000
//
//            // Gọi API Open-Meteo
//            val url = "$BASE_URL?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,weathercode&forecast_days=16"
//            val request = Request.Builder().url(url).build()
//            val client = OkHttpClient()
//            val response = client.newCall(request).execute()
//
//            if (response.isSuccessful) {
//                val json = response.body?.string()
//                val jsonObject = JSONObject(json)
//                val hourly = jsonObject.getJSONArray("hourly")
//                val timeArray = hourly.getJSONArray(0)
//                val tempArray = hourly.getJSONArray(1)
//                val weatherCodeArray = hourly.getJSONArray(2)
//
//                // Tìm giá trị gần nhất với ngày targetDate
//                var closestIndex = 0
//                var minDiff = Long.MAX_VALUE
//                for (i in 0 until timeArray.length()) {
//                    val time = dateFormat.parse(timeArray.getString(i))?.time ?: continue
//                    val diff = Math.abs(time - targetDate)
//                    if (diff < minDiff) {
//                        minDiff = diff
//                        closestIndex = i
//                    }
//                }
//
//                val temperature = tempArray.getDouble(closestIndex)
//                val weatherCode = weatherCodeArray.getInt(closestIndex)
//                val weatherDescription = getWeatherDescription(weatherCode)
//                "Nhiệt độ: ${temperature.toInt()}°C, $weatherDescription"
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
//
//    // Hàm ánh xạ weathercode sang mô tả (dựa trên tài liệu Open-Meteo)
//    private fun getWeatherDescription(code: Int): String {
//        return when (code) {
//            0 -> "Trời quang"
//            1, 2, 3 -> "Nhiều mây"
//            45, 48 -> "Sương mù"
//            51, 53, 55 -> "Mưa nhỏ"
//            61, 63, 65 -> "Mưa vừa"
//            80, 81, 82 -> "Mưa rào"
//            else -> "Không xác định"
//        }
//    }
//
//    // Hàm tạm thời để ánh xạ tên thành phố sang tọa độ (cần cải tiến với Geocoding API)
//    private fun getCoordinates(city: String): Pair<Double, Double>? {
//        return when (city.lowercase()) {
//            "hà nội" -> Pair(21.0278, 105.8342)
//            "tp.hcm" -> Pair(10.7769, 106.7009)
//            "đà nẵng" -> Pair(16.0544, 108.2022)
//            else -> null
//        }
//    }
//}