package com.example.quicknotes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_history")
data class QuizHistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,

    val date: Long,

    val score: Int,

    val totalQuestions: Int,

    val quizJson: String
)