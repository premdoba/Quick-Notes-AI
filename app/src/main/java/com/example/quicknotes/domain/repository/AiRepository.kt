package com.example.quicknotes.domain.repository

import com.example.quicknotes.domain.model.Mcq
import com.example.quicknotes.domain.model.StudyNotes

interface AiRepository {
    suspend fun generateStudyMaterial(input: String, educationLevel: String): StudyNotes?
    suspend fun askDoubt(question: String, educationLevel: String, contextText: String, historyText: String): String
    suspend fun generateMcqs(input: String, educationLevel: String, difficulty: String): Pair<List<Mcq>, String>?
}