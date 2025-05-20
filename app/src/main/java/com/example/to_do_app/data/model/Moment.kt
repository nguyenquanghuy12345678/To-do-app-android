package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moments")
data class Moment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val imagePath: String? = null // Đường dẫn đến ảnh trong bộ nhớ nội bộ
)