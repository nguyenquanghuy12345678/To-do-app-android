package com.example.to_do_app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.to_do_app.data.model.Moment

@Dao
interface MomentDao {
    @Query("SELECT * FROM moments ORDER BY date DESC")
    fun getAllMoments(): LiveData<List<Moment>>

    @Insert
    suspend fun insert(moment: Moment)

    @Update
    suspend fun update(moment: Moment)

    @Delete
    suspend fun delete(moment: Moment)
}