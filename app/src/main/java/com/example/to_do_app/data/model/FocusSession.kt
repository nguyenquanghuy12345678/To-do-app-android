package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: String = "", // Định dạng yyyy-MM-dd HH:mm:ss
    val durationMinutes: Int = 0, // Thời gian thực tế đã tập trung (phút)
    val status: String = ""     // "completed" hoặc "stopped"
)

//    val startTime: String = "", // Định dạng yyyy-MM-dd HH:mm:ss
//    val endTime: String = "",   // Định dạng yyyy-MM-dd HH:mm:ss
//    val durationMinutes: Int = 0, // Thời gian tập trung (phút)
//    val status: String = ""     // "completed" hoặc "cancelled"
