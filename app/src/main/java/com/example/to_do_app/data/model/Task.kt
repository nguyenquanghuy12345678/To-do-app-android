package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val startTime: String, // Định dạng: "HH:mm"
    val endTime: String, // Định dạng: "HH:mm"
    val date: String, // Định dạng: "yyyy-MM-dd"
    val status: String, // Ví dụ: "Pending", "Ongoing", "Completed"
    val tagId: Int? = null, // Liên kết với Tag
)




// val address: String = "" // Thêm trường địa chỉ