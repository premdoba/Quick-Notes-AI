package com.example.quicknotes.domain.model

data class QuizHistory(
    val id: Int = 0,
    val title: String,
    val date: Long,
    val score: Int,
    val totalQuestions: Int,
    val quizJson: String
)