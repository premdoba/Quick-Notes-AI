package com.example.quicknotes.domain.repository

import com.example.quicknotes.domain.model.StudyHistory
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    fun getAllHistory(): Flow<List<StudyHistory>>
    suspend fun insertHistory(item: StudyHistory)
    suspend fun getById(id: Int): StudyHistory?
    suspend fun deleteAll()
    suspend fun deleteById(id: Int)
}