package com.example.quicknotes.data.repository

import com.example.quicknotes.data.local.TodoDao
import com.example.quicknotes.data.mapper.toDomain
import com.example.quicknotes.data.mapper.toEntity
import com.example.quicknotes.domain.model.Todo
import com.example.quicknotes.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val dao: TodoDao
) : TodoRepository {

    override fun getAllTodos(): Flow<List<Todo>> {
        return dao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTodo(todo: Todo): Long {
        return dao.insertTodo(todo.toEntity())
    }

    override suspend fun updateTodo(todo: Todo) {
        dao.updateTodo(todo.toEntity())
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo.toEntity())
    }

    override suspend fun clearAllTodos() {
        dao.clearAllTodos()
    }
}