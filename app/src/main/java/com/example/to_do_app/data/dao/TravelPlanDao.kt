package com.example.to_do_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.to_do_app.data.model.TravelPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelPlanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)   // tự dộng thay thế bảng ghi có cùng id -> update
    suspend fun insert(travelPlan: TravelPlan)

    @Query("SELECT * FROM travel_plans")
    fun getAllTravelPlans(): Flow<List<TravelPlan>>

    @Query("DELETE FROM travel_plans WHERE id = :id")
    suspend fun deleteById(id: Int)
}