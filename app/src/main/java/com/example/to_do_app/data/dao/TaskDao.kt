package com.example.to_do_app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.to_do_app.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("SELECT * FROM tasks WHERE tagId = :tagId ORDER BY date, startTime")
    fun getTasksByTag(tagId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY date DESC LIMIT 5")
    fun getRecentTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime")
    fun getTasksByDate(date: String): LiveData<List<Task>>


//    @Query("SELECT * FROM tasks WHERE date LIKE :date || '%'")
//    fun getTasksByDate(date: String): LiveData<List<Task>>

//    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime")
//    fun getTasksByDate(date: String): Flow<List<Task>>
}