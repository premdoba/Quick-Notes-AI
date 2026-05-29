package com.example.quicknotes.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoEntity): Long

    @Update
    suspend fun updateTodo(todo: TodoEntity)

    @Delete
    suspend fun deleteTodo(todo: TodoEntity)

    @Query("DELETE FROM todo_table")
    suspend fun clearAllTodos()

    @Query("SELECT * FROM todo_table ORDER BY completed ASC, createdAt DESC")
    fun getAllTodos(): Flow<List<TodoEntity>>
}