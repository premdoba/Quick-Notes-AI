package com.example.quicknotes.repository

import com.example.quicknotes.data.local.TodoDao
import com.example.quicknotes.data.local.TodoEntity

class TodoRepository(
    private val dao: TodoDao
) {

    val allTodos = dao.getAllTodos()

    suspend fun insert(todo: TodoEntity): Long {
        return dao.insertTodo(todo)
    }

    suspend fun update(todo: TodoEntity) {
        dao.updateTodo(todo)
    }

    suspend fun delete(todo: TodoEntity) {
        dao.deleteTodo(todo)
    }

    suspend fun clearAll() {
        dao.clearAllTodos()
    }
}