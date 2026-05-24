package com.example.quicknotes.data.model

data class SavedQuiz(
    val question: String,
    val options: List<String>,
    val answer: String,
    val explanation: String,
    val selectedAnswer: String?
)