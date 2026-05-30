package com.example.quicknotes.domain.repository

import com.example.quicknotes.domain.model.QuizHistory
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    suspend fun insertQuiz(quiz: QuizHistory)
    fun getAllQuizHistory(): Flow<List<QuizHistory>>
    suspend fun deleteQuiz(id: Int)
    suspend fun clearQuizHistory()
    fun getQuizById(id: Int): Flow<QuizHistory?>
}