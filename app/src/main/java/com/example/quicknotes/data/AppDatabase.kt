package com.example.quicknotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknotes.data.local.QuizHistoryDao
import com.example.quicknotes.data.local.QuizHistoryEntity
import com.example.quicknotes.data.local.TodoDao
import com.example.quicknotes.data.local.TodoEntity
import com.example.quicknotes.data.study.StudyDao
import com.example.quicknotes.data.study.StudyHistoryEntity

@Database(entities = [StudyHistoryEntity::class, QuizHistoryEntity::class, TodoEntity::class], version = 8)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDao(): StudyDao
    abstract fun quizHistoryDao(): QuizHistoryDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quicknotes_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}