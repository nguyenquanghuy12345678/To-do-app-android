package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val createdAt: String, // Định dạng: "yyyy-MM-dd HH:mm:ss"
    val tagId: Int? = null // Liên kết với Tag
)