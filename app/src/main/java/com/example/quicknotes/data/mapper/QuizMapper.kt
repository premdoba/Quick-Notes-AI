package com.example.quicknotes.data.mapper

import com.example.quicknotes.data.local.QuizHistoryEntity
import com.example.quicknotes.domain.model.QuizHistory

fun QuizHistoryEntity.toDomain(): QuizHistory {
    return QuizHistory(
        id = id,
        title = title,
        date = date,
        score = score,
        totalQuestions = totalQuestions,
        quizJson = quizJson
    )
}

fun QuizHistory.toEntity(): QuizHistoryEntity {
    return QuizHistoryEntity(
        id = id,
        title = title,
        date = date,
        score = score,
        totalQuestions = totalQuestions,
        quizJson = quizJson
    )
}