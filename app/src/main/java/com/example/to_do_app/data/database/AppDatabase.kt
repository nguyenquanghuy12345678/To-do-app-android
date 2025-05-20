package com.example.to_do_app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.to_do_app.data.dao.TravelPlanDao
import com.example.to_do_app.data.dao.FocusSessionDao
import com.example.to_do_app.data.dao.MomentDao
import com.example.to_do_app.data.dao.MoodDao
import com.example.to_do_app.data.dao.NoteDao
import com.example.to_do_app.data.dao.TaskDao
import com.example.to_do_app.data.dao.UserDao
import com.example.to_do_app.data.model.FocusSession
import com.example.to_do_app.data.model.Moment
import com.example.to_do_app.data.model.Mood
import com.example.to_do_app.data.model.Note
import com.example.to_do_app.data.model.Task
import com.example.to_do_app.data.model.User
import com.example.to_do_app.data.model.Tag
import com.example.to_do_app.data.model.TravelPlan



@Database(entities = [Note::class, Task::class, User::class,Tag::class, Moment::class, FocusSession::class, Mood::class, TravelPlan::class], version = 15, exportSchema = false)

//@Database(entities = [Note::class, Task::class, User::class, Tag::class, Moment::class, FocusSession::class, Mood::class, TravelPlan::class], version = 13, exportSchema = false)

// tang verson khi thay doi cau truc models ,version = 2 //
// verson 4 với moments
// verson 6 với focus timer  (không dùng )
// verson 7 với focus timer
// verson 8 với test focus timer -> migration
// verson 9 với Mood -> cảm xúc
// verson 10 với sai vượt quá 5 item
// verson 11 với sai lỗi import packet MainActivity Enter 1 dòng
// versob 12: ok
// verson 13 với travel planer
// verson 14 voi Tag
// verson 15 với isWorkLocation trong TravelPlan

abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun userDao(): UserDao
    abstract fun momentDao(): MomentDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun moodDao(): MoodDao
    abstract fun travelPlanDao(): TravelPlanDao
}