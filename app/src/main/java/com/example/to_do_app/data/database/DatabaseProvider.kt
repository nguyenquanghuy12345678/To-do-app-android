package com.example.to_do_app.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "note_task_database"
            )
            .fallbackToDestructiveMigration() // thêm dòng này   - tranh crash
            .build().also { instance = it }
        }
    }
}