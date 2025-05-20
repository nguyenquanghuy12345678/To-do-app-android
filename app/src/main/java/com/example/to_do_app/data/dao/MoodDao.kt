package com.example.to_do_app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.to_do_app.data.model.Mood

@Dao
interface MoodDao {
    @Query("SELECT * FROM moods ORDER BY date DESC")
    fun getAllMoods(): LiveData<List<Mood>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: Mood)

    @Query("SELECT * FROM moods WHERE date = :date")
    suspend fun getMoodByDate(date: String): Mood?

    @Query("SELECT COUNT(*) FROM moods WHERE moodType = :moodType AND date >= :startDate AND date <= :endDate")
    suspend fun getMoodCount(moodType: String, startDate: String, endDate: String): Int
}