package com.example.quicknotes.domain.model

data class StudyHistory(
    val id: Int = 0,
    val topic: String,
    val educationLevel: String,
    val shortNotes: String,
    val questions: String,
    val mcqsRaw: String,
    val summary: String,
    val originalInput: String,
    val createdAt: Long = System.currentTimeMillis()
)