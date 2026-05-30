package com.example.quicknotes.data.mapper

import com.example.quicknotes.data.study.StudyHistoryEntity
import com.example.quicknotes.domain.model.StudyHistory

fun StudyHistoryEntity.toDomain(): StudyHistory {
    return StudyHistory(
        id = id,
        topic = topic,
        educationLevel = educationLevel,
        shortNotes = shortNotes,
        questions = questions,
        mcqsRaw = mcqsRaw,
        summary = summary,
        originalInput = originalInput,
        createdAt = createdAt
    )
}

fun StudyHistory.toEntity(): StudyHistoryEntity {
    return StudyHistoryEntity(
        id = id,
        topic = topic,
        educationLevel = educationLevel,
        shortNotes = shortNotes,
        questions = questions,
        mcqsRaw = mcqsRaw,
        summary = summary,
        originalInput = originalInput,
        createdAt = createdAt
    )
}