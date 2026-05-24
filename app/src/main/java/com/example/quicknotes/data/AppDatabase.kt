package com.example.quicknotes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknotes.data.local.QuizHistoryDao
import com.example.quicknotes.data.local.QuizHistoryEntity

@Database(entities = [StudyHistoryEntity::class, QuizHistoryEntity::class], version = 7)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDao(): StudyDao
    abstract fun quizHistoryDao(): QuizHistoryDao

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