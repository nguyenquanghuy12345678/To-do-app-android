package com.example.to_do_app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.to_do_app.data.Converters
import java.io.Serializable

@Entity(tableName = "travel_plans")
@TypeConverters(Converters::class)
data class TravelPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budget: Double,
    val todoList: List<String>,
    val placesToVisit: List<String>


    ,
    // Thêm mới: Trường để phân biệt kế hoạch du lịch và địa điểm làm việc
    val isWorkLocation: Boolean = false
) : Serializable