package com.example.quicknotes.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizHistoryEntity)

    @Query("SELECT * FROM quiz_history ORDER BY date DESC")
    fun getAllQuizHistory(): Flow<List<QuizHistoryEntity>>

    @Query("DELETE FROM quiz_history WHERE id = :id")
    suspend fun deleteQuiz(id: Int)

    @Query("DELETE FROM quiz_history")
    suspend fun clearQuizHistory()

    @Query("SELECT * FROM quiz_history WHERE id = :id")
    fun getQuizById(id: Int): Flow<QuizHistoryEntity?>
}