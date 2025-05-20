package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class Mood(
    @PrimaryKey val date: String, // Định dạng yyyy-MM-dd
    val moodType: String = "", // Loại tâm trạng: "Happy", "Sad", "Neutral", "Stressed", "Angry"
    val note: String = "" // Ghi chú (tùy chọn)
)