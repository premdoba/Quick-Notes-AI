package com.example.quicknotes.data.study

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_history")
data class StudyHistoryEntity(
    @PrimaryKey(autoGenerate = true)
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