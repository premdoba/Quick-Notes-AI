package com.example.quicknotes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    @Insert
    suspend fun insertHistory(item: StudyHistoryEntity)

    @Query("SELECT * FROM study_history ORDER BY createdAt DESC")
    fun getAllHistory(): Flow<List<StudyHistoryEntity>>

    @Query("SELECT * FROM study_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): StudyHistoryEntity?

    @Query("DELETE FROM study_history")
    suspend fun deleteAll()

    @Query("DELETE FROM study_history WHERE id = :id")
    suspend fun deleteById(id: Int)
}