package com.example.to_do_app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.to_do_app.data.model.FocusSession

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY startTime DESC")
    fun getAllFocusSessions(): LiveData<List<FocusSession>>

    @Insert
    suspend fun insert(focusSession: FocusSession)
}