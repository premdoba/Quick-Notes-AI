package com.example.quicknotes.data.repository

import com.example.quicknotes.data.local.QuizHistoryDao
import com.example.quicknotes.data.mapper.toDomain
import com.example.quicknotes.data.mapper.toEntity
import com.example.quicknotes.domain.model.QuizHistory
import com.example.quicknotes.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuizRepositoryImpl(
    private val dao: QuizHistoryDao
) : QuizRepository {

    override suspend fun insertQuiz(quiz: QuizHistory) {
        dao.insertQuiz(quiz.toEntity())
    }

    override fun getAllQuizHistory(): Flow<List<QuizHistory>> {
        return dao.getAllQuizHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun deleteQuiz(id: Int) {
        dao.deleteQuiz(id)
    }

    override suspend fun clearQuizHistory() {
        dao.clearQuizHistory()
    }

    override fun getQuizById(id: Int): Flow<QuizHistory?> {
        return dao.getQuizById(id).map { it?.toDomain() }
    }
}